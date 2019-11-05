package arduino.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SubActivity extends AppCompatActivity {

    private EditText editText_name;
    private EditText editText_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub);

        this.getEditTextObject();
    }

    public void getEditTextObject()
    {
        editText_name = (EditText)findViewById(R.id.editText_name);
        editText_number = (EditText)findViewById(R.id.editText_number);
    }

    public void OnClickHandle(View view)
    {
        Intent resultIntent = new Intent();

        resultIntent.putExtra("name", editText_name.getText().toString());
        resultIntent.putExtra("number", editText_number.getText().toString());

        //setResult(Code.resultCode, resultIntent);
        finish();
    }
}