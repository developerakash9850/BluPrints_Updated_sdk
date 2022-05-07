package com.mindorks.framework.bluprints_updated_sdk;

import static bluprints.printer_sdk_1_0_0_23.Printer.ESCAPE_CENTER;
import static bluprints.printer_sdk_1_0_0_23.Printer.ESCAPE_DOUBLE_HEIGHT;
import static bluprints.printer_sdk_1_0_0_23.Printer.ESCAPE_EXCL;
import static bluprints.printer_sdk_1_0_0_23.Printer.ESCAPE_SEQ;
import static bluprints.printer_sdk_1_0_0_23.Printer.ESCAPE_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import bluprints.printer_sdk_1_0_0_23.CardReader;
import bluprints.printer_sdk_1_0_0_23.CardScanner;
import bluprints.printer_sdk_1_0_0_23.Printer;
import bluprints.printer_sdk_1_0_0_23.Scrybe;
import bluprints.printer_sdk_1_0_0_23.ScrybeDevice;


public class MainActivity extends AppCompatActivity implements CardScanner, Scrybe  {

    ScrybeDevice m_AemScrybeDevice;
    CardReader m_cardReader = null;
    Printer m_AemPrinter = null;
    ArrayList<String> printerList;
    int glbPrinterWidth = 32;
    private Thread thread = null;
    int numChars = glbPrinterWidth;
    int n_serial_number = 0;
    int n_bt_conneCt_count = 0;
    Button btn_defualt_settings = null;
    Button btn_fwupdate_eneble = null;
    Button prn_config_btn = null;
    Button discoverButton = null;
    TextView txtBatteryStatus;
    public Handler mHandler;
    String[] responseArray = new String[1];
    Spinner spinner;
    String creditData;
    EditText editText,rfText;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printerList = new ArrayList<String>();
        creditData = new String();
        m_AemScrybeDevice = new ScrybeDevice(this);
        editText = (EditText) findViewById(R.id.edittext);
        txtBatteryStatus=(TextView) findViewById(R.id.txtBatteryStatus);
        spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.printer_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                    glbPrinterWidth=48;
                    onSetPrinterType(view);
                }
                else{
                    glbPrinterWidth=32;
                    onSetPrinterType(view);
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        Button discoverButton = (Button) findViewById(R.id.pairing);
        registerForContextMenu(discoverButton);


    }

    public void onSetPrinterType(View v) {
        if(glbPrinterWidth == 32) {
            glbPrinterWidth = 32;
            //showAlert("32 Characters / Line or 2 Inch (58mm) Printer Selected!");

        } else {
            glbPrinterWidth = 48;
           // showAlert("48 Characters / Line or 3 Inch (80mm) Printer Selected!");
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Printer to connect");
        for (int i = 0; i < printerList.size(); i++) {
            menu.add(0, v.getId(), 0, printerList.get(i));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        String printerName = item.getTitle().toString();
        try {
            m_AemScrybeDevice.connectToPrinter(printerName);
            m_cardReader = m_AemScrybeDevice.getCardReader(this);
            m_AemPrinter = m_AemScrybeDevice.getAemPrinter();
            Toast.makeText(MainActivity.this,"Connected with " + printerName,Toast.LENGTH_SHORT ).show();
        }
        catch (IOException e) { if (e.getMessage().contains("Service discovery failed")) {
                Toast.makeText(MainActivity.this,"Not Connected\n"+ printerName + " is unreachable or off otherwise it is connected with other device",Toast.LENGTH_SHORT ).show();
            } else if (e.getMessage().contains("Device or resource busy")) {
                Toast.makeText(MainActivity.this,"the device is already connected",Toast.LENGTH_SHORT ).show();
            } else {
                Toast.makeText(MainActivity.this,"Unable to connect",Toast.LENGTH_SHORT ).show();
            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        if (m_AemScrybeDevice != null) {
            try {
                m_AemScrybeDevice.disConnectPrinter();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    public void onShowPairedPrinters(View v) {
        //   String p = m_AemScrybeDevice.pairPrinter("BTprinter0314");
        String p = m_AemScrybeDevice.pairPrinter("BTprinter0314");
        // showAlert(p);
        printerList = m_AemScrybeDevice.getPairedPrinters();
        if (printerList.size() > 0)
            openContextMenu(v);
       /* else
            showAlert("No Paired Printers found");*/
    }


    @Override
    public void onScanMSR(String buffer, CardReader.CARD_TRACK cardtrack) {

    }



    @Override
    public void onScanDLCard(String s) {

    }

    @Override
    public void onScanRCCard(String s) {

    }

    @Override
    public void onScanRFD(String s) {

    }

    @Override
    public void onScanPacket(String s) {

    }

    @Override
    public void onDiscoveryComplete(ArrayList<String> arrayList) {

    }

    public void onPrintBill(View v) {
        int numChars = glbPrinterWidth;//CheckPrinterWidth();
        Toast.makeText(MainActivity.this, "Printing " + numChars + " Character/Line Bill", Toast.LENGTH_SHORT).show();
        onPrintBillBluetooth(numChars);
    }

    public void onPrintBillBluetooth(int numChars) {
        if (m_AemPrinter == null) {
            Toast.makeText(MainActivity.this, "Printer not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = "TWO INCH PRINTER: TEST PRINT \n";
        String d =    "_________________________________\n";
        try {
            if(numChars == 32) {
                m_AemPrinter.setFontType(Printer.FONT_NORMAL);
                m_AemPrinter.setFontType(Printer.TEXT_ALIGNMENT_CENTER);
                m_AemPrinter.print(data);
                m_AemPrinter.print(d);
                data = "CODE|DESC|RATE(Rs)|QTY |AMT(Rs)\n";
                m_AemPrinter.print(data);
                m_AemPrinter.print(d);
                data = "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "13|ColgateGel |35.00|02|70.00\n" +
                        "29|Pears Soap |25.00|01|25.00\n" +
                        "88|Lux Shower |46.00|01|46.00\n" +
                        "15|Dabur Honey|65.00|01|65.00\n" +
                        "52|Dairy Milk |20.00|10|200.00\n" +
                        "128|Maggie TS |36.00|04|144.00\n" +
                        "_______________________________\n";
                m_AemPrinter.print(data);
                m_AemPrinter.POS_FontThreeInchCENTER();
                data = "TOTAL AMOUNT (Rs.)550.00\n";
                m_AemPrinter.print(data);
                m_AemPrinter.POS_FontThreeInchCENTER();
                data = "Thank you! \n";
                m_AemPrinter.print(data);
                m_AemPrinter.setLineFeed(17);

            } else {
                m_AemPrinter.setFontType(ESCAPE_SEQ); //esc
                m_AemPrinter.setFontType(ESCAPE_a); //a
                m_AemPrinter.setFontType(ESCAPE_CENTER); //0x01
                m_AemPrinter.setFontType(ESCAPE_SEQ); //esc
                m_AemPrinter.setFontType(ESCAPE_EXCL); //!
                m_AemPrinter.setFontType(ESCAPE_DOUBLE_HEIGHT); //esc
                data = "THREE INCH PRINTER: TEST PRINT \n";
                m_AemPrinter.print(data);
                m_AemPrinter.setLineFeed(1);
                data = 	"CODE|   DESCRIPTION   |RATE(Rs)|QTY |AMOUNT(Rs)\n";
                m_AemPrinter.print(data);

                data =  " 13 |Colgate Total Gel | 35.00  | 02 |  70.00\n"+
                        " 29 |Pears Soap 250g   | 25.00  | 01 |  25.00\n"+
                        " 88 |Lux Shower Gel 500| 46.00  | 01 |  46.00\n"+
                        " 15 |Dabur Honey 250g  | 65.00  | 01 |  65.00\n"+
                        " 52 |Cadbury Dairy Milk| 20.00  | 10 | 200.00\n"+
                        "128 |Maggie Totamto Sou| 36.00  | 04 | 144.00\n";
                m_AemPrinter.POS_FontThreeInchTAHOMA();
                m_AemPrinter.POS__FontThreeInchInitialize_printer();
                m_AemPrinter.print(data);
                data = "           TOTAL AMOUNT (Rs.)   550.00\n";
                m_AemPrinter.print(data);
                data = "Thank you! \n";
                m_AemPrinter.POS_FontThreeInchCENTER();
                m_AemPrinter.print(data);
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
            }
        }
        catch (IOException e) {
            if (e.getMessage().contains("socket closed"))
                Toast.makeText(MainActivity.this,"Printer not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPrintMultilingual(View v) {
        if (m_AemPrinter == null) {
            Toast.makeText(MainActivity.this, "Printer not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = editText.getText().toString();

        if (data.isEmpty()) {
         //   showAlert("Write Text");
        } else {
            try {
                if (glbPrinterWidth==32) {
                    m_AemPrinter.printTextAsImage(data);
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                }
                else {
                    m_AemPrinter.printTextAsImageThreeInch(data);
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPrintRasterImage(View v) {
        selectImage();
    }
    public void selectImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_CAPTURE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && null !=data){
            Uri selectedImageUri = data.getData();
            uriToBitmap(selectedImageUri);

        } else {
            Toast.makeText(MainActivity.this, "You have not selected and image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            Bitmap resizedBitmap=null;
            if(glbPrinterWidth == 32)
                resizedBitmap = Bitmap.createScaledBitmap(image, 384, 577, false);
            else
                resizedBitmap = Bitmap.createScaledBitmap(image, 577, 577, false);
            // resizedBitmap = Bitmap.createScaledBitmap(image, 384, 384, false);
            if (m_AemPrinter == null) {
                Toast.makeText(MainActivity.this, "Printer not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            RasterBT(resizedBitmap);
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void RasterBT(Bitmap image) {
        try {
            if (glbPrinterWidth == 32) {
                m_AemPrinter.printImage(image, getApplicationContext(), Printer.IMAGE_CENTER_ALIGNMENT);
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();

            } else {

                m_AemPrinter.printImageThreeInch(image);
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
                m_AemPrinter.setCarriageReturn();
            }
        }
        catch (IOException e) {
         //   showAlert("IO EX:  " + e.toString());
        }
    }

    public void onPrintBarcode(View v) {
        onPrintBarcodeBT();
    }
    public void onPrintBarcodeBT() {
        if (m_AemPrinter == null) {
          //  showAlert("Printer not connected");
            return;
        }
        String text = editText.getText().toString();
        if (text.isEmpty()) {
           // showAlert("Write Text TO Generate Barcode");
        } else {
            try {
                if(glbPrinterWidth==32) {
                    m_AemPrinter.POS_FontThreeInchCENTER();
                    m_AemPrinter.printBarcode(text, Printer.BARCODE_TYPE.CODE39, Printer.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                } else {
                    m_AemPrinter.printBarcodeThreeInch(text, Printer.BARCODE_TYPE.CODE39, Printer.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
                    m_AemPrinter.POS_FontThreeInchCENTER();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();

                }
            } catch (IOException e) {
               // showAlert("Printer not connected");
            }
        }

    }

    public void onPrintQRCodeRaster(View v) throws WriterException, IOException {
        if (m_AemPrinter == null) {
            Toast.makeText(MainActivity.this, "Printer not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        String text= editText.getText().toString();
        if(text.isEmpty()){
            Toast.makeText(MainActivity.this, "Write Text To Generate QR Code", Toast.LENGTH_SHORT).show();
           // showAlert("Write Text To Generate QR Code");
        }
        else {
            Writer writer = new QRCodeWriter();
            String finalData = Uri.encode(text, "UTF-8");
          //  showAlert("QR " + text);
            try {

                BitMatrix bm = writer.encode(finalData, BarcodeFormat.QR_CODE, 300, 300);
                Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
                for (int i = 0; i < 300; i++) {
                    for (int j = 0; j < 300; j++) {
                        bitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
                //showAlert("generating qr bitmap " + wifiConnection);
                Bitmap resizedBitmap = null;
                int numChars = glbPrinterWidth;
                if(numChars == 32){
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, 384, 384, false);
                    m_AemPrinter.printImage(resizedBitmap);
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();

                }else {
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, 384, 430, false);
                    m_AemPrinter.printImageThreeInch(resizedBitmap);
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                }
            } catch (WriterException e) {
              //  showAlert("Error WrQR: " + e.toString());
            }
        }
    }

    public void onPrintImage(View v) {
        onPrintImageBT();
    }

    private void onPrintImageBT() {
        try
        {
            InputStream is = getAssets().open("bluprintlogo1.jpg");
            Bitmap inputBitmap = BitmapFactory.decodeStream(is);
            Bitmap resizedBitmap = null;
            if(glbPrinterWidth == 32)
                resizedBitmap = Bitmap.createScaledBitmap(inputBitmap, 384, 384, false);

            else
                resizedBitmap = Bitmap.createScaledBitmap(inputBitmap, 384, 384, false);
            //m_AemPrinter.printBitImage(resizedBitmap,BluetoothActivity.this,m_AemPrinter.IMAGE_CENTER_ALIGNMENT);

            RasterBT(resizedBitmap);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onDisconnectDevice(View view) {
        if (m_AemScrybeDevice != null) {
            try {
                m_AemScrybeDevice.disConnectPrinter();
                Toast.makeText(MainActivity.this, "disconnected", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onPrintReverseText(View v) {
        if (m_AemPrinter == null) {
            Toast.makeText(MainActivity.this, "Printer not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = editText.getText().toString();
        if (data.isEmpty()) {
          //  showAlert("Write Text");
        } else {
            try {
                if (glbPrinterWidth==32) {
                    m_AemPrinter.printReverseText(data);
                    m_AemPrinter.setLineFeed(12);
                }
                else {
                    m_AemPrinter.printReverseText(data);
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                    m_AemPrinter.setCarriageReturn();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}