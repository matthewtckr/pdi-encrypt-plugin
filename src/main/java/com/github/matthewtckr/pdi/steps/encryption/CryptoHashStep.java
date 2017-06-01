package com.github.matthewtckr.pdi.steps.encryption;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class CryptoHashStep extends BaseStep implements StepInterface  {

  private CryptoHashStepMeta meta;
  private CryptoHashStepData data;

  public CryptoHashStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
      Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  @Override
  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    if ( !super.init( smi, sdi ) ) {
      return false;
    }
    meta = (CryptoHashStepMeta) smi;
    data = (CryptoHashStepData) sdi;
    return true;
  }

  @Override
  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    Object[] r = getRow();

    if ( r == null ) {
      setOutputDone();
      return false;
    }

    if ( first ) {
      first = false;
      data.inputFieldIndex = getInputRowMeta().indexOfValue( meta.getInputFieldName() );
      data.outputRowMeta = getInputRowMeta().clone();
      meta.getFields( data.outputRowMeta, getStepname(), null, null, this, repository, metaStore );
      data.outputFieldIndex = data.outputRowMeta.indexOfValue( meta.getOutputFieldName() );
    }

    Object[] outputRow = RowDataUtil.resizeArray( r, data.outputRowMeta.size() );

    String password = (String) r[data.inputFieldIndex];
    String result = data.encrypt( password, meta.getCryptoHashType(), meta.getCost() );

    if ( data.outputFieldIndex >= 0 ) {
      outputRow[data.outputFieldIndex] = result;
    }
    putRow( data.outputRowMeta, outputRow );
    return true;
  }
}
