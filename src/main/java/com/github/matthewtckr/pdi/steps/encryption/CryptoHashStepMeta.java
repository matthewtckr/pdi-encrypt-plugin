package com.github.matthewtckr.pdi.steps.encryption;

import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step(
    id = "ComGithubMatthewtckrCryptoHash",
    image = "com/github/matthewtckr/pdi/steps/encryption.png",
    i18nPackageName = "com.github.matthewtckr.pdi.steps.encryption",
    name = "CryptoHashStep.Name",
    description = "CryptoHashStep.TooltipDesc",
    categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Cryptography"
  )
public class CryptoHashStepMeta extends BaseStepMeta implements StepMetaInterface {

  public static int DEFAULT_COST = 12;
  public static enum CryptoHashType {
    BCRYPT {
      @Override
      public String toString() {
        return "BCrypt";
      }
    },
    SHA3 {
      @Override
      public String toString() {
        return "SHA-3";
      }
    }
  }

  private int cost;
  private CryptoHashType cryptoHashType;
  private String inputFieldName;
  private String outputFieldName;

  public int getCost() {
    return cost;
  }

  public void setCost( int cost ) {
    this.cost = cost;
  }

  public CryptoHashType getCryptoHashType() {
    return cryptoHashType;
  }

  public void setCryptoHashType( CryptoHashType cryptoHashType ) {
    this.cryptoHashType = cryptoHashType;
  }

  public String getInputFieldName() {
    return inputFieldName;
  }

  public void setInputFieldName( String fieldname ) {
    this.inputFieldName = fieldname;
  }

  public String getOutputFieldName() {
    return outputFieldName;
  }

  public void setOutputFieldName( String fieldname ) {
    this.outputFieldName = fieldname;
  }

  @Override
  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
      TransMeta transMeta, Trans trans ) {
    return new CryptoHashStep( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  @Override
  public StepDataInterface getStepData() {
    return new CryptoHashStepData();
  }

  @Override
  public void setDefault() {
    setCost( DEFAULT_COST );
    setCryptoHashType( CryptoHashType.BCRYPT );
  }

  @Override
  public void getFields( RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
      VariableSpace space, Repository repository, IMetaStore metaStore ) throws KettleStepException {
    super.getFields( inputRowMeta, name, info, nextStep, space, repository, metaStore );
    inputRowMeta.addValueMeta( new ValueMetaString( space.environmentSubstitute( getOutputFieldName() ) ) );
  }

  @Override
  public String getXML() throws KettleException {
    StringBuilder builder = new StringBuilder();
    builder.append( super.getXML() );
    builder.append( "    " ).append( XMLHandler.addTagValue( "cryptoHashType",
      getCryptoHashType() == null ? null : getCryptoHashType().name() ) );
    builder.append( "    " ).append( XMLHandler.addTagValue( "cost", getCost() ) );
    builder.append( "    " ).append( XMLHandler.addTagValue( "inputFieldName", getInputFieldName() ) );
    builder.append( "    " ).append( XMLHandler.addTagValue( "outputFieldName", getOutputFieldName() ) );
    return builder.toString();
  }

  @Override
  public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
    super.loadXML( stepnode, databases, metaStore );
    String encryptTypeValue = XMLHandler.getTagValue( stepnode, "cryptoHashType" );
    if ( !Const.isEmpty( encryptTypeValue ) ) {
      setCryptoHashType( CryptoHashType.valueOf( encryptTypeValue ) );
    } else {
      setCryptoHashType( null );
    }
    setCost( new Integer( XMLHandler.getTagValue( stepnode, "cost" ) ) );
    setInputFieldName( XMLHandler.getTagValue( stepnode, "inputFieldName" ) );
    setOutputFieldName( XMLHandler.getTagValue( stepnode, "outputFieldName" ) );
  }

  @Override
  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
    throws KettleException {
    super.readRep( rep, metaStore, id_step, databases );
    String encryptTypeValue = rep.getStepAttributeString( id_step, "cryptoHashType" );
    if ( !Const.isEmpty( encryptTypeValue ) ) {
      setCryptoHashType( CryptoHashType.valueOf( encryptTypeValue ) );
    } else {
      setCryptoHashType( null );
    }
    setCost( new Integer( rep.getStepAttributeString( id_step, "cost" ) ) );
    setInputFieldName( rep.getStepAttributeString( id_step, "inputFieldName" ) );
    setOutputFieldName( rep.getStepAttributeString( id_step, "outputFieldName" ) );
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    super.saveRep( rep, metaStore, id_transformation, id_step );
    rep.saveStepAttribute( id_transformation, id_step, "cryptoHashType",
      getCryptoHashType() == null ? null : getCryptoHashType().name() );
    rep.saveStepAttribute( id_transformation, id_step, "cost", getCost() );
    rep.saveStepAttribute( id_transformation, id_step, "inputFieldName", getInputFieldName() );
    rep.saveStepAttribute( id_transformation, id_step, "outputFieldName", getOutputFieldName() );
  }
}
