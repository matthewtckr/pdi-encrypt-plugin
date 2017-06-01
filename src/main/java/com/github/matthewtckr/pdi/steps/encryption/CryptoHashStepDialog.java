package com.github.matthewtckr.pdi.steps.encryption;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.LabelTextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import com.github.matthewtckr.pdi.steps.encryption.CryptoHashStepMeta.CryptoHashType;

public class CryptoHashStepDialog extends BaseStepDialog implements StepDialogInterface {

  private static final Class<?> PKG = CryptoHashStepMeta.class;
  private CryptoHashStepMeta input;

  private Label wlEncryptType, wlInputFieldname;
  private CCombo wEncryptType, wInputFieldname;
  private LabelTextVar wOutputFieldname;

  public CryptoHashStepDialog( Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname ) {
    super( parent, (BaseStepMeta) baseStepMeta, transMeta, stepname );
    input = (CryptoHashStepMeta) baseStepMeta;
  }

  @Override
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };
    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "CryptoHashStepDialog.Shell.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Stepname line
    wlStepname = new Label( shell, SWT.RIGHT );
    wlStepname.setText( BaseMessages.getString( PKG, "CryptoHashStepDialog.Stepname.Label" ) );
    props.setLook( wlStepname );
    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment( 0, 0 );
    fdlStepname.right = new FormAttachment( middle, -margin );
    fdlStepname.top = new FormAttachment( 0, margin );
    wlStepname.setLayoutData( fdlStepname );
    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wStepname.setText( stepname );
    props.setLook( wStepname );
    wStepname.addModifyListener( lsMod );
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment( middle, 0 );
    fdStepname.top = new FormAttachment( 0, margin );
    fdStepname.right = new FormAttachment( 100, 0 );
    wStepname.setLayoutData( fdStepname );

    // Encrypt Type Field Name
    wlEncryptType = new Label( shell, SWT.RIGHT );
    wlEncryptType.setText( BaseMessages.getString( PKG, "CryptoHashStepDialog.EncryptTypeFieldname.Label" ) );
    wlEncryptType.setToolTipText( BaseMessages.getString( PKG, "CryptoHashStepDialog.EncryptTypeFieldname.Tooltip" ) );
    props.setLook( wlEncryptType );
    FormData fdlEncryptType = new FormData();
    fdlEncryptType.left = new FormAttachment( 0, 0 );
    fdlEncryptType.top = new FormAttachment( wStepname, 2 * margin );
    fdlEncryptType.right = new FormAttachment( middle, -margin );
    wlEncryptType.setLayoutData( fdlEncryptType );

    wEncryptType = new CCombo( shell, SWT.BORDER | SWT.READ_ONLY );
    props.setLook( wEncryptType );
    wEncryptType.addModifyListener( lsMod );
    FormData fdEncryptType = new FormData();
    fdEncryptType.left = new FormAttachment( middle, 0 );
    fdEncryptType.top = new FormAttachment( wStepname, margin );
    fdEncryptType.right = new FormAttachment( 100, -margin );
    wEncryptType.setLayoutData( fdEncryptType );

    List<String> encryptTypes = new ArrayList<String>();
    for ( CryptoHashType downloadType : CryptoHashType.values() ) {
      encryptTypes.add( downloadType.toString() );
    }
    wEncryptType.setItems( encryptTypes.toArray( new String[encryptTypes.size()] ) );

    RowMetaInterface previousFields;
    try {
      previousFields = transMeta.getPrevStepFields( stepMeta );
    } catch ( KettleStepException e ) {
      new ErrorDialog( shell,
        BaseMessages.getString( PKG, "CryptoHashStepDialog.ErrorDialog.UnableToGetInputFields.Title" ),
        BaseMessages.getString( PKG, "CryptoHashStepDialog.ErrorDialog.UnableToGetInputFields.Message" ), e );
      previousFields = new RowMeta();
    }

    // Incoming Field Name
    wlInputFieldname = new Label( shell, SWT.RIGHT );
    wlInputFieldname.setText( BaseMessages.getString( PKG, "CryptoHashStepDialog.InputFieldname.Label" ) );
    wlInputFieldname.setToolTipText( BaseMessages.getString( PKG, "CryptoHashStepDialog.InputFieldname.Tooltip" ) );
    props.setLook( wlInputFieldname );
    FormData fdlInputFieldname = new FormData();
    fdlInputFieldname.left = new FormAttachment( 0, 0 );
    fdlInputFieldname.top = new FormAttachment( wEncryptType, 2 * margin );
    fdlInputFieldname.right = new FormAttachment( middle, -margin );
    wlInputFieldname.setLayoutData( fdlInputFieldname );

    wInputFieldname = new CCombo( shell, SWT.BORDER | SWT.READ_ONLY );
    props.setLook( wInputFieldname );
    wInputFieldname.addModifyListener( lsMod );
    FormData fdInputFieldname = new FormData();
    fdInputFieldname.left = new FormAttachment( middle, 0 );
    fdInputFieldname.top = new FormAttachment( wEncryptType, margin );
    fdInputFieldname.right = new FormAttachment( 100, -margin );
    wInputFieldname.setLayoutData( fdInputFieldname );
    wInputFieldname.setItems( previousFields.getFieldNames() );
    wInputFieldname.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
      }
    } );

    // Output Field Name
    wOutputFieldname = new LabelTextVar( transMeta, shell,
      BaseMessages.getString( PKG, "CryptoHashStepDialog.OutputFieldname.Label" ),
      BaseMessages.getString( PKG, "CryptoHashStepDialog.OutputFieldname.Tooltip" ) );
    props.setLook( wOutputFieldname );
    wOutputFieldname.addModifyListener( lsMod );
    FormData fdOutputFieldname = new FormData();
    fdOutputFieldname.left = new FormAttachment( 0, -margin );
    fdOutputFieldname.top = new FormAttachment( wInputFieldname, 2 * margin );
    fdOutputFieldname.right = new FormAttachment( 100, -margin );
    wOutputFieldname.setLayoutData( fdOutputFieldname );

    // Some buttons
    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOK, wCancel }, margin, wOutputFieldname );

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOK.addListener( SWT.Selection, lsOK );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wStepname.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  private void cancel() {
    stepname = null;
    input.setChanged( changed );
    dispose();
  }

  private void ok() {
    if ( Const.isEmpty( wStepname.getText() ) ) {
      return;
    }

    // Get the information for the dialog into the input structure.
    getInfo( input );

    dispose();
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    if ( null == input.getCryptoHashType() ) {
      input.setChanged();
      wEncryptType.setText( CryptoHashType.BCRYPT.toString() );
    } else {
      wEncryptType.setText( input.getCryptoHashType().toString() );
    }
    wInputFieldname.setText( Const.NVL( input.getInputFieldName(), "" ) );
    wOutputFieldname.setText( Const.NVL( input.getOutputFieldName(), "" ) );

    wStepname.selectAll();
    wStepname.setFocus();
  }

  private void getInfo( CryptoHashStepMeta inf ) {
    inf.setCryptoHashType( CryptoHashType.values()[wEncryptType.getSelectionIndex()] );
    inf.setInputFieldName( wInputFieldname.getText() );
    inf.setOutputFieldName( wOutputFieldname.getText() );
    stepname = wStepname.getText(); // return value
  }
}
