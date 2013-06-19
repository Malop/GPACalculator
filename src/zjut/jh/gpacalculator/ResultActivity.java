package zjut.jh.gpacalculator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.gpacalculator.R;

import zjut.jh.gpacalculator.Bean.ScoreBean;
import zjut.jh.gpacalculator.Bean.StudentInfoBean;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity{

	/*private String[] courses;
	private String[] credits;
	private String[] scores;*/
	private ArrayList<HashMap<String,String>> listItem;
	private ListView mylistview;
	private Button back;
	private TextView totaljidian,totalcredit,avajidian;
	private TextView stdIdView,stdNameView,stdClassView,stdTermView;
	
	private String mTerm;
	private StudentInfoBean mStd;
	private List<ScoreBean> mScoreList;
	
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		setTitle("查询结果");
		
		mTerm = getIntent().getStringExtra("term");
		mStd = (StudentInfoBean)getIntent().getSerializableExtra("student");
		mScoreList =  (List<ScoreBean>) getIntent().getSerializableExtra("score");
		
		stdIdView = (TextView) findViewById(R.id.stdId);
		stdNameView = (TextView) findViewById(R.id.stdName);
		stdClassView = (TextView) findViewById(R.id.stdClass);
		stdTermView = (TextView) findViewById(R.id.stdTerm);
		
		stdIdView.setText(mStd.getStudentId());
		stdNameView.setText(mStd.getStudentName());
		stdClassView.setText(mStd.getStudentClass());
		stdTermView.setText("学期："+mTerm);
		
 		totaljidian = (TextView) findViewById(R.id.totaljidian);
		totalcredit = (TextView) findViewById(R.id.totalcredit);
		avajidian = (TextView) findViewById(R.id.avajidian);
		
		back = (Button)findViewById(R.id.result_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		init();
		calculate();
		
		mylistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "您选择了课程"+mScoreList.get(arg2).getCourse(), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void calculate() {
		// TODO Auto-generated method stub
		double jidian=0,ava,credit=0;
		for(int i=0;i<mScoreList.size();i++){
			/*if(mScoreList.get(i).getCredit().trim().equals("")){
				System.out.println("学分为空格字符");
				mScoreList.get(i).setCredit("0");
			}*/
			if(mScoreList.get(i).getCredit().trim().equals("0.5")||
				mScoreList.get(i).getCredit().trim().equals("1")||
				mScoreList.get(i).getCredit().trim().equals("2")||
				mScoreList.get(i).getCredit().trim().equals("2.5")||
				mScoreList.get(i).getCredit().trim().equals("3")||
				mScoreList.get(i).getCredit().trim().equals("4")||
				mScoreList.get(i).getCredit().trim().equals("5")||
				mScoreList.get(i).getCredit().trim().equals("6")||
				mScoreList.get(i).getCredit().trim().equals("7")||
				mScoreList.get(i).getCredit().trim().equals("8")){
				
				credit+=Double.parseDouble(mScoreList.get(i).getCredit());
				
			}else{
				System.out.println("学分为其他字符");
				mScoreList.get(i).setCredit("0");
				//credit+=Double.parseDouble("0");
			}
			
		}
		for(int i=0;i<mScoreList.size();i++){
			if(mScoreList.get(i).getScore().equals("优秀")){
				mScoreList.get(i).setScore("95");
			}if(mScoreList.get(i).getScore().equals("良好")){
				mScoreList.get(i).setScore("85");
			}if(mScoreList.get(i).getScore().equals("中等")){
				mScoreList.get(i).setScore("75");
			}if(mScoreList.get(i).getScore().equals("及格")){
				mScoreList.get(i).setScore("60");
			}if(mScoreList.get(i).getScore().equals("不及格")){
				mScoreList.get(i).setScore("50");
			}if(mScoreList.get(i).getScore().equals("事假")){
				mScoreList.get(i).setScore("0");
			}
			jidian+=((Double.parseDouble(mScoreList.get(i).getScore())-50)/10)*Double.parseDouble(mScoreList.get(i).getCredit());
			
		}
		ava = jidian/credit;
		DecimalFormat df=new DecimalFormat(".##");//使double保留两位小数
		totaljidian.setText(df.format(jidian));
		totalcredit.setText(Double.toString(credit));
		avajidian.setText(df.format(ava));
	}

	private void init() {
		// TODO Auto-generated method stub
		mylistview = (ListView)findViewById(R.id.lv);
		listItem = loadData();
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.list_item, new String[]{"course","credit","score"},
				new int[]{R.id.course,R.id.credit,R.id.score});
		mylistview.setAdapter(listItemAdapter);
	}

	private ArrayList<HashMap<String, String>> loadData() {
		// TODO Auto-generated method stub
		listItem = new ArrayList<HashMap<String,String>>();
		for(int i=0;i<mScoreList.size();i++){
			HashMap<String,String> map = new HashMap<String,String>();
			String course = mScoreList.get(i).getCourse();
			String credit = mScoreList.get(i).getCredit();
			String score = mScoreList.get(i).getScore();
			map.put("course", course);
			map.put("credit", credit);
			map.put("score", score);
			listItem.add(map);
		}
		return listItem;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
