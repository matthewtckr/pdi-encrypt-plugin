package com.github.matthewtckr.pdi.steps.encryption;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleStepException;

import com.github.matthewtckr.pdi.steps.encryption.CryptoHashStepMeta.CryptoHashType;

public class CryptoHashStepDataTest {

  private CryptoHashStepData data;

  @Before
  public void setup() {
    data = new CryptoHashStepData();
  }

  @Test
  public void testBCrypt() throws KettleStepException {
    String encryptedPassword = data.bcrypt( "password" );
    assertThat( encryptedPassword, notNullValue() );
    assertThat( encryptedPassword.length(), either( equalTo( 59 ) ).or( equalTo( 60 ) ) );
    assertThat( encryptedPassword, startsWith( "$2a$" ) );

    encryptedPassword = data.bcrypt( "61626300", 9 );
    assertThat( encryptedPassword, notNullValue() );
    assertThat( encryptedPassword.length(), either( equalTo( 59 ) ).or( equalTo( 60 ) ) );
    assertThat( encryptedPassword, startsWith( "$2a$09$" ) );
    
    byte[] salt = new BigInteger( 127, new Random() ).toByteArray(); // Random 16-byte salt
    encryptedPassword = data.bcrypt( "61626300", salt, 9 );
    assertThat( encryptedPassword, notNullValue() );
    assertThat( encryptedPassword.length(), either( equalTo( 59 ) ).or( equalTo( 60 ) ) );
    assertThat( encryptedPassword, startsWith( "$2a$09$" ) );
  }

  @Test
  public void testBCryptCheck() {
	String[][] testCases = new String[][] {
      { "password", "$2a$11$M5TNbv3ZQuhloazIj8dro.DxZ5cP.Z.rRXJbiCZTX.dlxpTWA8Jti" },
      { "Pentaho", "$2a$04$nqq3.caaLydWXg5XaiSRweXUZqioPE3rWIaUAb9zDGiBBOUWkjiYO" },
	};
	for ( String[] testCase : testCases ) {
      assertThat( CryptoHashStepData.bcryptCheck( testCase[1], testCase[0] ), is( true ) );
	}
  }

  @Test
  public void testSha3() {
    String[][] testCases = new String[][] {
    	{ "", "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a" },
    	{ "password", "c0067d4af4e87f00dbac63b6156828237059172d1bbeac67427345d6a9fda484" },
    };
    for ( String[] testCase : testCases ) {
      assertThat( data.sha3( testCase[0] ), equalTo( testCase[1] ) );
    }
  }

  @Test
  public void testEncrypt() throws KettleStepException {
    assertThat( data.encrypt( "123456", (CryptoHashType) null, 12 ), equalTo( "123456" ) );
    for ( CryptoHashType method : CryptoHashType.values() ) {
      assertThat( data.encrypt( "123456", method, 12 ), not( equalTo( "123456" ) ) );
    }
  }
}
