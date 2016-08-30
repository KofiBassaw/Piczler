package com.piczler.piczler;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONObject;


public class Database {
	
	public static final String ID="id";
	public static final String TYPE="data_type";
	public static final String SERVER_ID="server_id";
	public static final String JSONSTRING="jsonString";


	
	
	
	
	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_NAME="piczler_db";
	private static final String TABLE_SAMPLE_DETAILS="tb_sample_details";



private DbHandler ourHandler;
private final Context mContext;
private SQLiteDatabase ourDatabase;


private class DbHandler extends SQLiteOpenHelper {

	public DbHandler(Context context, String DATABASE_NAME, SQLiteDatabase.CursorFactory k, int DATABASE_VERSION) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

        //we ar adding audio table


        db.execSQL("CREATE TABLE "+TABLE_SAMPLE_DETAILS+" ( "+
                ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                SERVER_ID+ " TEXT NOT NULL, "+
                TYPE+ " INTEGER NOT NULL, "+
                JSONSTRING+ " TEXT NOT NULL, "+
                " CONSTRAINT dist_const UNIQUE ("+SERVER_ID+" ,"+TYPE+"));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub


	}
	
}


public Database(Context c){
	mContext=c;
}

public Database open()throws SQLException {
	ourHandler=new DbHandler(mContext, DATABASE_NAME, null, DATABASE_VERSION);
	ourDatabase=ourHandler.getWritableDatabase();
	return this;
}

public void close()throws SQLException {
	ourDatabase.close();
	
}




    public void deleteSampleJson(String serverID, int type) throws SQLException
    {
       ourDatabase.delete(TABLE_SAMPLE_DETAILS,TYPE+"="+type+" AND "+SERVER_ID+"="+serverID,null);
    }


    public void insertSampleDetails(String serverID, int type, String jsonString)throws SQLException
    {


        try
        {
            String sql = "";



                sql = "INSERT OR REPLACE INTO "+ TABLE_SAMPLE_DETAILS +"("+SERVER_ID+", "+
                        TYPE+", "+
                        JSONSTRING+") VALUES (?,?,?);";

            SQLiteStatement statement = ourDatabase.compileStatement(sql);


            ourDatabase.beginTransaction();
            try{
                statement.clearBindings();

                statement.bindString(1, serverID);
                statement.bindString(2, ""+type);
                statement.bindString(3, jsonString);
                statement.execute();
            }catch (Exception e){
                e.printStackTrace();
            }


            ourDatabase.setTransactionSuccessful();
            ourDatabase.endTransaction();
        }catch (Exception ed)
        {
            ed.printStackTrace();
        }
    }


    public Cursor getSampleDetails(int type, String serverId)throws SQLException
    {
       String query = "SELECT * FROM "+ TABLE_SAMPLE_DETAILS+" WHERE "+SERVER_ID+"=? AND "+TYPE+"= ?";
        String params[] ={serverId,""+type};
       return ourDatabase.rawQuery(query,params);
    }



    public void deleteAllData()
    {
        ourDatabase.delete(TABLE_SAMPLE_DETAILS,null,null);
    }



}
