package com.github.matthewtckr.pdi.steps.encryption;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransTestFactory;

import com.github.matthewtckr.pdi.steps.encryption.CryptoHashStepMeta.CryptoHashType;

public class CryptoHashStepIT {

  private static final String STEP_NAME = "Crypto Step";
  private static final String INPUT_FIELDNAME = "testInput";
  private static final String EXPECTED_FIELDNAME = "testExpected";
  private static final String OUTPUT_FIELDNAME = "testOutput";

  private CryptoHashStepMeta meta;

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }

  private static List<RowMetaAndData> createInputDataSha3() {
    RowMetaInterface rm = new RowMeta();
    rm.addValueMeta( new ValueMetaString( INPUT_FIELDNAME ) );
    rm.addValueMeta( new ValueMetaString( EXPECTED_FIELDNAME ) );
    List<RowMetaAndData> rmd = new ArrayList<RowMetaAndData>();
    rmd.add( new RowMetaAndData( rm,
      new Object[]{ "test1", "48000d754ed116dfc5719bf5b037dd7245fc9272b822f68c892a1ef30d37079b" } ) );
    rmd.add( new RowMetaAndData( rm,
      new Object[]{ "test2", "f61895df023b4f74cb48f6ebfec37e9d215661a32ea10ffa176b7cf27e746e38" } ) );
    rmd.add( new RowMetaAndData( rm,
      new Object[]{ "test3", "a4e4a9b7dcb37cebaf82f5285aeb0192773de7fa670dbb0a780c6274ba326695" } ) );
    return rmd;
  }

  private static List<RowMetaAndData> createInputDataBCrypt() {
    RowMetaInterface rm = new RowMeta();
    rm.addValueMeta( new ValueMetaString( INPUT_FIELDNAME ) );
    List<RowMetaAndData> rmd = new ArrayList<RowMetaAndData>();
    rmd.add( new RowMetaAndData( rm, new Object[]{ "test1" } ) );
    rmd.add( new RowMetaAndData( rm, new Object[]{ "test2" } ) );
    rmd.add( new RowMetaAndData( rm, new Object[]{ "test3" } ) );
    return rmd;
  }

  @Before
  public void setup() {
    meta = new CryptoHashStepMeta();
    meta.setDefault();
    meta.setInputFieldName( INPUT_FIELDNAME );
    meta.setOutputFieldName( OUTPUT_FIELDNAME );
  }

  @Test
  public void testSHA3() throws KettleException {
    meta.setCryptoHashType( CryptoHashType.SHA3 );

    TransMeta trans = TransTestFactory.generateTestTransformation( null, meta, STEP_NAME );
    List<RowMetaAndData> input = createInputDataSha3();

    List<RowMetaAndData> result = TransTestFactory.executeTestTransformation( trans,
      TransTestFactory.INJECTOR_STEPNAME, STEP_NAME, TransTestFactory.DUMMY_STEPNAME, input );

    assertTrue( result.size() == 3 );
    assertTrue( validateOutput( result, meta.getCryptoHashType() ) );
  }

  @Test
  public void testBCrypt() throws KettleException {
    meta.setCryptoHashType( CryptoHashType.BCRYPT );
    meta.setCost( CryptoHashStepMeta.BCRYPT_MINIMUM_COST ); // Lower cost used for faster test

    TransMeta trans = TransTestFactory.generateTestTransformation( null, meta, STEP_NAME );
    List<RowMetaAndData> input = createInputDataBCrypt();

    List<RowMetaAndData> result = TransTestFactory.executeTestTransformation( trans,
      TransTestFactory.INJECTOR_STEPNAME, STEP_NAME, TransTestFactory.DUMMY_STEPNAME, input );

    assertTrue( result.size() == 3 );
    assertTrue( validateOutput( result, meta.getCryptoHashType() ) );
  }

  private static boolean validateOutput( List<RowMetaAndData> dataset, CryptoHashType type ) {
    if ( dataset == null || dataset.size() == 0 ) {
      return false;
    }
    RowMetaInterface rowMeta = dataset.get( 0 ).getRowMeta();
    int inputFieldIndex = rowMeta.indexOfValue( INPUT_FIELDNAME );
    int expectedFieldIndex = rowMeta.indexOfValue( EXPECTED_FIELDNAME );
    int actualFieldIndex = rowMeta.indexOfValue( OUTPUT_FIELDNAME );
    for ( int i = 0; i < dataset.size(); i++ ) {
      RowMetaAndData rmd = dataset.get( i );
      String original = UUID.randomUUID().toString();
      String expected = UUID.randomUUID().toString();
      String actual = UUID.randomUUID().toString();
      try {
        if ( inputFieldIndex >= 0 ) {
          original = rmd.getString( INPUT_FIELDNAME, null );
        }
        if ( expectedFieldIndex >= 0 ) {
          expected = rmd.getString( EXPECTED_FIELDNAME, null );
        }
        if ( actualFieldIndex >= 0 ) {
          actual = rmd.getString( OUTPUT_FIELDNAME, null );
        }
      } catch ( KettleValueException e ) {
        return false;
      }
      switch( type ) {
      case SHA3:
        if ( !expected.equals( actual ) ) {
          return false;
        }
        break;
      case BCRYPT:
        if ( !CryptoHashStepData.bcryptCheck( actual, original ) ) {
          return false;
        }
        break;
      }
    }
    return true;
  }
}
