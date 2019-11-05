package arduino.bluetooth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        db.execSQL("CREATE TABLE LEVINFO( _id INTEGER PRIMARY KEY AUTOINCREMENT," + " level INTEGER);");
        db.execSQL("INSERT INTO LEVINFO VALUES (null , 1 );");

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + "LEVINFO");
        onCreate(db);
    }

    public void insert(int lev) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO LEVINFO VALUES (null , lev);");

        db.close();
    }

    public void update(int lev) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE LEVINFO SET level = "+ (lev) +" WHERE level = "+ (lev) +";");
        db.close();
    }



    public int getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        int result=0;

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM LEVINFO", null);
        while (cursor.moveToNext()) {
            result += cursor.getInt(1);
        }


        return result;
    }
}

