package com.github.matthewtckr.pdi.steps.encryption;

import java.util.Random;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.bouncycastle.jcajce.provider.digest.SHA3.Digest256;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.bouncycastle.util.encoders.Hex;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.github.matthewtckr.pdi.steps.encryption.CryptoHashStepMeta.CryptoHashType;

public class CryptoHashStepData extends BaseStepData implements StepDataInterface  {

  private static final int BCRYPT_SALT_SIZE_BYTES = 16;
  private static final int BCRYPT_COST_DEFAULT = 12;
  private Random rand = new Random();
  private DigestSHA3 sha3Digest;

  RowMetaInterface outputRowMeta;
  int inputFieldIndex;
  int outputFieldIndex;

  String encrypt( String password, CryptoHashType method, int cost ) throws KettleStepException {
    if ( null == method ) {
      return password;
    }
    switch ( method ) {
      case BCRYPT:
        return bcrypt( password, cost );
      case SHA3:
        return sha3( password );
      default:
        throw new KettleStepException( method + " is not implemented" );
    }
  }

  String bcrypt( String password ) throws KettleStepException {
    return bcrypt( password, BCRYPT_COST_DEFAULT );
  }

  String bcrypt( String password, int cost ) throws KettleStepException {
    return bcrypt( password, getSaltBcrypt(), cost );
  }

  String bcrypt( String password, byte[] salt, int cost ) throws KettleStepException {
    String retval = null;
    try {
      retval = OpenBSDBCrypt.generate( password.toCharArray(), salt, cost );
    } catch ( IllegalArgumentException iae ) {
      throw new KettleStepException( iae );
    }
    return retval;
  }

  private byte[] getSaltBcrypt() {
    byte[] temp = new byte[BCRYPT_SALT_SIZE_BYTES];
    rand.nextBytes( temp );
    return temp;
  }

  static boolean bcryptCheck( String hash, String password ) {
    return OpenBSDBCrypt.checkPassword( hash, password.toCharArray() );
  }

  String sha3( String password ) {
    if ( null == sha3Digest ) {
      sha3Digest = new Digest256();
    }
    sha3Digest.reset();
    sha3Digest.update( password.getBytes() );
    return Hex.toHexString( sha3Digest.digest() );
  }
}
