package com.sainath.rentalapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PostMainActivity extends ActionBarActivity {

	int REQUEST_CAMERA = 0, SELECT_FILE = 1;
	Button btnSelect;
	ImageView ivImage;
    Button btnDone;
    EditText mEdit;
    String textTitle;
    String textCategory;
    String textDesc;
    String textPrice;
    String textLoc;
    String textName;
    String textEmail;
    String textPhone;
    public String TAG ="CaptureImage";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.title_activity_post);

        /*btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
		btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });*/
		ivImage = (ImageView) findViewById(R.id.ivImage);
		ivImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnDone = (Button)findViewById(R.id.done);
        btnDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Saved", Toast.LENGTH_LONG).show();
                mEdit = (EditText)findViewById(R.id.title);
                textTitle = mEdit.getText().toString();
              //  Log.v(TAG, textTitle);
                mEdit = (EditText)findViewById(R.id.category);
                textCategory = mEdit.getText().toString();
              //  Log.v(TAG, textCategory);
                mEdit = (EditText)findViewById(R.id.desc);
                textDesc = mEdit.getText().toString();
              //  Log.v(TAG, textDesc);
                mEdit = (EditText)findViewById(R.id.price);
                textPrice = mEdit.getText().toString();
                //  Log.v(TAG, textPrice);
                mEdit = (EditText)findViewById(R.id.location);
                textLoc = mEdit.getText().toString();
             //   Log.v(TAG, textLoc);
                /*mEdit = (EditText)findViewById(R.id.name);
                textName = mEdit.getText().toString();
             //   Log.v(TAG, textName);
                mEdit = (EditText)findViewById(R.id.email);
                textEmail = mEdit.getText().toString();
             //   Log.v(TAG, textEmail);
                mEdit = (EditText)findViewById(R.id.phone);
                textPhone = mEdit.getText().toString();*/
              //  Log.v(TAG, textPhone);
                fillDb();
            }
        });

	}

    private void fillDb() {
        ContentValues values = new ContentValues();
        values.put("POST_TITLE", textTitle );

        values.put("POST_DESCRIPTION",textDesc );// Test
        values.put("POST_PRICE", Integer.parseInt(textPrice));
        values.put("POST_CATEGORY", textCategory);
        values.put("POST_LOCATION", textLoc);
		/*values.put("POST_NAME",textName);
		values.put("POST_EMAIL",textEmail);
		values.put("POST_MOBILE", Integer.parseInt(textPhone));*/

		getApplicationContext().getContentResolver().insert(Uri.parse("content://com.database.rentalapp/POST_TABLE"),values);

        /*Cursor rental_cursor = getApplicationContext().getContentResolver().query(Uri.parse("content://com.rentalapp/POST_TABLE"), null, null, null, null);
        if(rental_cursor != null && rental_cursor.moveToFirst())
        {
            Toast.makeText(getApplicationContext(), "Rental Data - "+rental_cursor.getString(rental_cursor.getColumnIndex("POST_TITLE")), Toast.LENGTH_SHORT).show();
            Log.v(TAG, rental_cursor.getString(rental_cursor.getColumnIndex("POST_TITLE")));
            Log.v(TAG, rental_cursor.getString(rental_cursor.getColumnIndex("POST_DESCRIPTION")));
            Log.v(TAG, rental_cursor.getString(rental_cursor.getColumnIndex("POST_PRICE")));
            Log.v(TAG, rental_cursor.getString(rental_cursor.getColumnIndex("POST_CATEGORY")));
            Log.v(TAG, rental_cursor.getString(rental_cursor.getColumnIndex("POST_LOCATION")));
            rental_cursor.close();
        }*/
		finish();
    }

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(PostMainActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE)
				onSelectFromGalleryResult(data);
			else if (requestCode == REQUEST_CAMERA)
				onCaptureImageResult(data);
		}
	}

	private void onCaptureImageResult(Intent data) {
		Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

		File destination = new File(Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".jpg");

		FileOutputStream fo;
		try {
			destination.createNewFile();
			fo = new FileOutputStream(destination);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ivImage.setImageBitmap(thumbnail);
	}

	@SuppressWarnings("deprecation")
	private void onSelectFromGalleryResult(Intent data) {
		Uri selectedImageUri = data.getData();
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
				null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		String selectedImagePath = cursor.getString(column_index);

		Bitmap bm;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(selectedImagePath, options);
		final int REQUIRED_SIZE = 200;
		int scale = 1;
		while (options.outWidth / scale / 2 >= REQUIRED_SIZE
				&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
			scale *= 2;
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(selectedImagePath, options);

		ivImage.setImageBitmap(bm);
	}

}
