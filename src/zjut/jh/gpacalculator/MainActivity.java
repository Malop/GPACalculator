package zjut.jh.gpacalculator;

import java.io.Serializable;
import java.util.List;

import com.example.gpacalculator.R;

import zjut.jh.gpacalculator.Bean.ScoreBean;
import zjut.jh.gpacalculator.Bean.StudentInfoBean;
import zjut.jh.gpacalculator.net.HtmlPaser;
import zjut.jh.gpacalculator.net.HttpUtil;
import zjut.jh.gpacalculator.net.NetConstant;
import zjut.jh.gpacalculator.net.NetworkUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ProgressDialog pd;// 进度条对话框
	int count = 0;
	private boolean isLogin;
	private int termFlag = 0;
	private String[] term = NetConstant.term;
	private String queryViewState;// 查询时要截取的viewstate
	private String strUserId;
	private String strPassword;
	private HttpUtil myHttpUtil;
	private HtmlPaser queryHtmlPaser, viewStateHtmlPaser;
	private StudentInfoBean studentBean;
	private List<ScoreBean> scoreList;

	private CheckBox savePasswordCB;
	private SharedPreferences sp;// 用SharePreference来记住帐号，密码

	private AutoCompleteTextView userId;
	private EditText password;
	private Spinner termAddress;
	private Button calculate;
	private ImageButton close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		termAddress = (Spinner) this.findViewById(R.id.spinner_term);
		ArrayAdapter trem_adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, term);
		trem_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		termAddress.setAdapter(trem_adapter);

		termAddress.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int flag, long arg3) {
				termFlag = flag;
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		userId = (AutoCompleteTextView) findViewById(R.id.userid);
		password = (EditText) this.findViewById(R.id.password);
		calculate = (Button) this.findViewById(R.id.calculate);
		close = (ImageButton) this.findViewById(R.id.close);
		if (!NetworkUtils.isNetWorkValiable(this)) {
			Toast.makeText(this, "亲，你忘了连接网络啦！", Toast.LENGTH_LONG).show();
		}

		sp = this.getSharedPreferences("passwordFile", MODE_PRIVATE);
		savePasswordCB = (CheckBox) findViewById(R.id.savePasswordCB);
		savePasswordCB.setChecked(true);// 默认为记住密码
		userId.setThreshold(1);// 输入1个字母就开始自动提示
		userId.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				password.setText(sp.getString(userId.getText().toString(), ""));// 自动输入密码
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				String[] allUserName = new String[sp.getAll().size()];// sp.getAll().size()返回的是有多少个键值对
				allUserName = sp.getAll().keySet().toArray(new String[0]);
				// sp.getAll()返回一张hash map
				// keySet()得到的是a set of the keys.
				// hash map是由key-value组成的
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainActivity.this,
						android.R.layout.simple_dropdown_item_1line,
						allUserName);
				userId.setAdapter(adapter);// 设置数据适配器
			}

		});

		calculate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				strUserId = userId.getText().toString();
				strPassword = password.getText().toString();
				if (strUserId.equals("") || strPassword.equals("")) {
					new AlertDialog.Builder(MainActivity.this)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("警告").setMessage("请输入学号或密码")
							.setPositiveButton("确定", null).show();
					return;
				} else {/*
						 * new
						 * AlertDialog.Builder(MainActivity.this).setIcon(android
						 * .R.drawable.ic_dialog_alert)
						 * .setTitle("结果").setMessage
						 * ("学号："+strUserId+"\n学期："+term
						 * [termFlag]+"\n网址："+net[netFlag])
						 * .setPositiveButton("确定", null).show(); return;
						 */

					// 进行数据的获取，计算，和传递到result中
					count = 0;
					// 创建ProgressDialog对象
					pd = new ProgressDialog(MainActivity.this);
					// 设置进度条风格，风格为长形
					pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					// 设置ProgressDialog 标题
					pd.setTitle("提示");
					// 设置ProgressDialog 提示信息
					pd.setMessage("正在查询！请稍候...");
					// 设置ProgressDialog 进度条进度
					pd.setProgress(100);
					// 设置ProgressDialog 的进度条是否不明确
					pd.setIndeterminate(false);
					// 设置ProgressDialog 是否可以按退回按键取消
					pd.setCancelable(false);
					// 让ProgressDialog显示
					pd.show();

					new Thread() {
						public void run() {
							try {
								while (count <= 100) {
									// 由线程来控制进度。
									pd.setProgress(count++);
									if (count == 24) {
										myHttpUtil = new HttpUtil(strUserId,strPassword, term[termFlag]);
										isLogin = myHttpUtil.login();
									}
									if (count == 54) {
										viewStateHtmlPaser = new HtmlPaser(myHttpUtil.getViewStateHtml());
										queryViewState = viewStateHtmlPaser.getQueryViewState();
									}
									if (count == 85 && isLogin == true) {
										queryHtmlPaser = new HtmlPaser(myHttpUtil.query(queryViewState));
										studentBean = queryHtmlPaser.getStudentInfo();
										scoreList = queryHtmlPaser.getScore();
									}
									Thread.sleep(100);
								}
								pd.cancel();
								// System.out.println("111111="+scoreList.size());

								Message msg = new Message();
								Bundle b1 = new Bundle();// 存放数据
								if (isLogin == false || studentBean == null) {
									// System.out.println("error=login函数出错了!");
									b1.putString("flg", "1");
									msg.setData(b1);
									myHandler.sendMessage(msg); // 向Handler发送消息,更新UI

								}
								if (scoreList.size() == 0) {
									b1.putString("flg", "2");
									msg.setData(b1);
									myHandler.sendMessage(msg); // 向Handler发送消息,更新UI

								} else {
									if (savePasswordCB.isChecked()) {// 登陆成功才保存密码
										sp.edit().putString(strUserId,strPassword).commit();
									}
									Intent resultIntent = new Intent(
											MainActivity.this,
											ResultActivity.class);
									resultIntent.putExtra("term",
											term[termFlag]);
									resultIntent.putExtra("student",
											studentBean);
									resultIntent.putExtra("score",
											(Serializable) scoreList);
									startActivity(resultIntent);

								}
							} catch (Exception e) {
								pd.cancel();
							}
						}
					}.start();
				}
			}
		});

		close.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				MainActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b2 = msg.getData();
			String flg = b2.getString("flg");
			if (flg.equals("1")) {
				new AlertDialog.Builder(MainActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("提示").setMessage("帐号密码错误，查询失败!")
						.setPositiveButton("确定", null).show();
			}
			if (flg.equals("2")) {
				new AlertDialog.Builder(MainActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("提示").setMessage("该学期木有你的成绩!")
						.setPositiveButton("确定", null).show();
			}
		}

	};
}
