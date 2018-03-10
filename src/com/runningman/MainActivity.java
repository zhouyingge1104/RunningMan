package com.runningman;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import com.runningman.util.ThreadUtil;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	final int HIDE_KEYBOARD = 11;
	private Timer timer;
	private TimerTask task;
	private boolean taskFlag = false;
	
	private ImageView ivWelcome;
	private LinearLayout layoutMain;
	private LinearLayout layoutSetting;
	private Button btnSetting;
	private Button btnSaveSetting;
	
	private TextView InputPassword;
	private TextView PasswordResult;
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Button btn5;
	private Button btn6;
	private Button btn7;
	private Button btn8;
	private Button btn9;
	private Button btn0;
	private Button btnEsc;
	private Button btnEnt;
	
	private EditText ServerIp;
	private EditText ServerPort;
	private EditText Password;
	  
	private Socket mSocket = null;
	private OutputStream ops = null;
	private InputStream ips = null;
	
	private byte[] back = new byte[9];
	
	private Handler h;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		
		//getWindow().setFlags(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);  //设置全屏  
		setContentView(R.layout.fragment_main);
		
		h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HIDE_KEYBOARD:
					
					task.cancel();
					
					ivWelcome.setVisibility(View.VISIBLE);
					layoutMain.setVisibility(View.INVISIBLE);
					break;
				
				default:
					super.handleMessage(msg);
				}
			}
		};
		
		layoutMain = (LinearLayout)findViewById(R.id.layout_main);
		//有点击就重新监听
		layoutMain.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				System.out.println("Touch事件：" + event.getAction());
				if(event.getAction() == MotionEvent.ACTION_DOWN){ 
					task.cancel();
					
					waitOperation();
					
		            return true;  
		        }  
				
				return false;
			}
		});
		//有点击就重新监听
		layoutMain.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.out.println("Click事件：");
				timer.cancel();
				timer.purge();
				waitOperation();
				
			}
		});
		
		layoutSetting = (LinearLayout)findViewById(R.id.layout_setting);
		
		InputPassword = (TextView)findViewById(R.id.tv_input_password);
		InputPassword.setBackgroundColor(Color.argb(0, 0, 0, 0)); //背景透明度
		
		PasswordResult = (TextView)findViewById(R.id.tv_password_result);
		PasswordResult.setBackgroundColor(Color.argb(0, 0, 0, 0)); //背景透明度
		
		ivWelcome = (ImageView)findViewById(R.id.imgview_welcome);
		ivWelcome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputPassword.setText(null);
				
				v.setVisibility(View.GONE);
				layoutMain.setVisibility(View.VISIBLE);
				
				waitOperation();
			}
		});
		
		
		
		btn1 = (Button)findViewById(R.id.btn_1); //btn1.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn2 = (Button)findViewById(R.id.btn_2); //btn2.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn3 = (Button)findViewById(R.id.btn_3); //btn3.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn4 = (Button)findViewById(R.id.btn_4); //btn4.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn5 = (Button)findViewById(R.id.btn_5); //btn5.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn6 = (Button)findViewById(R.id.btn_6); //btn6.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn7 = (Button)findViewById(R.id.btn_7); //btn7.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn8 = (Button)findViewById(R.id.btn_8); //btn8.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn9 = (Button)findViewById(R.id.btn_9); //btn9.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btn0 = (Button)findViewById(R.id.btn_0); //btn0.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btnEsc = (Button)findViewById(R.id.btn_esc); //btnEsc.setBackgroundColor(Color.argb(25, 0, 0, 0));
		btnEnt = (Button)findViewById(R.id.btn_ent); //btnEnt.setBackgroundColor(Color.argb(25, 0, 0, 0));
		
		ServerIp = (EditText)findViewById(R.id.et_server_ip);
	    ServerPort = (EditText)findViewById(R.id.et_server_port);
	    Password = (EditText)findViewById(R.id.et_password);
	    
	    
	    ServerIp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				task.cancel();
				waitOperation();
			}
		});
	    
	    ServerPort.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				task.cancel();
				waitOperation();
			}
		});
	    
	    Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				task.cancel();
				waitOperation();
			}
		});
	    
	    loadIpPort();
	    
	    btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "1");
				
				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "2");
				
				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "3");
				
				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "4");

				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "5");

				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "6");

				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn7.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "7");

				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn8.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "8");

				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn9.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "9");

				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btn0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputPassword.setText(InputPassword.getText() + "0");

				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
	    
	    btnEsc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				PasswordResult.setVisibility(View.GONE);
				InputPassword.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
				
				if(InputPassword.getText().toString().trim().equals("")){
					return;
				}else{
					InputPassword.setText(InputPassword.getText().subSequence(0, InputPassword.getText().length()-1));
				}
			}
		});
	    
	    btnEnt.setOnClickListener(new View.OnClickListener() {
	    	
	    	byte[] Open = {-1, 90, -2, 9, 21, 21, 2, 36, 57};
			 
		      Runnable runnable = new Runnable()
		      {
		        public void run()
		        {
		          Message localMessage = new Message();
		          localMessage.what = 1;
		          try
		          {

		        	mSocket = new Socket(InetAddress.getByName(ServerIp.getText().toString().trim()), Integer.parseInt(ServerPort.getText().toString().trim()));
		            //mSocket = new Socket(InetAddress.getByName("192.168.0.102"), Integer.parseInt("8899"));

		            ops = mSocket.getOutputStream();
		            ips = mSocket.getInputStream();
		            
		            ops.write(Open);
		            ips.read(back);
		            
		            System.out.println();
		            for(int i =0; i < back.length; i++){
		            	System.out.print(back[i] + " ");
		            	
		            }
		            System.out.println();
		            System.out.println();
		            
		            ips.close();
		            ops.close();
		            mSocket.close();
		            
		            return;
		          }
		          catch (Exception localException)
		          {
		            localException.printStackTrace();
		          }
		        }
		      };
	    	
			@Override
			public void onClick(View v) {
				
				task.cancel();
				waitOperation();
				
				SharedPreferences localSharedPreferences = getSharedPreferences("wifi relay", 0);
				String passwordRight = localSharedPreferences.getString("password", "123456");
				System.out.println("实际密码：" + passwordRight + ".");
				System.out.println("输入密码：" + InputPassword.getText() + ".");
				String passwordCurr = InputPassword.getText().toString();
				if(passwordCurr.equals(passwordRight)){
					//Toast.makeText(MainActivity.this, "密码正确 ^_^", Toast.LENGTH_SHORT).show();
					PasswordResult.setText("密码正确");
					PasswordResult.setVisibility(View.VISIBLE);
					InputPassword.setVisibility(View.GONE);
					
					new Thread(this.runnable).start();
					
				}else{
					//Toast.makeText(MainActivity.this, "密码错误 -_-'", Toast.LENGTH_SHORT).show();
					PasswordResult.setText("密码错误");
					PasswordResult.setVisibility(View.VISIBLE);
					InputPassword.setVisibility(View.GONE);
				}
				
			}
		});
	  
		
		btnSetting = (Button)findViewById(R.id.btn_setting);
		btnSetting.setBackgroundColor(Color.argb(0, 0, 0, 0));
		btnSetting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loadIpPort();
				layoutSetting.setVisibility(View.VISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
		
		btnSaveSetting = (Button)findViewById(R.id.btn_save_setting);
		btnSaveSetting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				saveIpPort();
				layoutSetting.setVisibility(View.INVISIBLE);
				
				task.cancel();
				waitOperation();
			}
		});
			
		
	}

	public void loadIpPort()
	{
	   SharedPreferences localSharedPreferences = getSharedPreferences("wifi relay", 0);
	   ServerIp.setText(localSharedPreferences.getString("ip", "192.168.1.240"));
	   ServerPort.setText(localSharedPreferences.getString("port", "8899"));
	   Password.setText(localSharedPreferences.getString("password", "123456"));
	}
	
	public void saveIpPort()
	{
	   SharedPreferences.Editor localEditor = getSharedPreferences("wifi relay", 0).edit();
	   localEditor.putString("ip", ServerIp.getText().toString());
	   localEditor.putString("port", ServerPort.getText().toString());
	   localEditor.putString("password", Password.getText().toString());
	   localEditor.commit();
	}
	
	/**
	 * 监听，如果30秒没有操作就回到第一个界面
	 */
	private void waitOperation(){
		
		task = new TimerTask() {
            @Override
            public void run() {
            	//do the test ...
            	System.out.println("开始关闭操作界面");
            	h.sendEmptyMessage(HIDE_KEYBOARD); //发送OK标志，开始测试
            }
        };
		
		timer = new Timer(); 
		
		System.out.println("开始监听任务");
        long delay = 30000;  
        long intevalPeriod = 1; 
        
        
        
        
        //ThreadUtil.sleep(3000);
        
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.setting) {
			//to do something...
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
