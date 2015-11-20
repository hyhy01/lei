package com.example.lei;

import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import asia.jeremie.Core;
import asia.jeremie.Flag;

public class MainActivity extends Activity {
	Button[] Btn = null;
	Core core = new Core();
	int xcount = 9, ycount = 9;
int longing=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setView(xcount, ycount);
		core.initial(xcount, ycount, xcount* ycount / 4, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; thisadds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setView(int x, int y) {
		// 获取屏幕大小，以合理设定 按钮 大小及位置

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		 //自定义layout组件
		RelativeLayout layout = new RelativeLayout(this);

		 //这里创建16个按钮，每行放置4个按钮

		Btn = new Button[x* y];
		int j = -1;
		for (int i = 0; i < x*y; i++) {
			Btn[i] = new Button(this);
			Btn[i].setId(2000 + i);
			Btn[i].setText(i +"" );
			RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams(
					(width - 50) /(x), 40);  //设置按钮的宽度和高度
			if (i % x == 0) {
				j++;
			}
			btParams.leftMargin = ((width - 50) / x) * (i % x);  //横坐标定位
			btParams.topMargin = 40 *j;  //纵坐标定位
			layout.addView(Btn[i], btParams); // 将按钮放入layout组件
		}
		this.setContentView(layout);
		 //批量设置监听
		for (int k = 0; k <Btn.length; k++) {
			 //这里不需要findId，因为创建的时候已经确定哪个按钮对应哪个Id
			Btn[k].setTag(k);  //为按钮设置一个标记，来确认是按下了哪一个按钮

			Btn[k].setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					int i = (Integer) v.getTag();  //这里的i不能在外部定义，因为内部类的关系，内部类好多繁琐的东西，要好好研究一番
					if(longing==1){
						return;
					}
					System.out.println(i);
					Flag flag = core.Hit(i % ycount, i/ xcount);
					if (flag.equals(Flag.Boom)) {
						repaint(true);
					} else {
						repaint(false);
					}

				}
			});
			Btn[k].setLongClickable(true);
			Btn[k].setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					int i = (Integer) v.getTag();
					longing=i;
					Toast.makeText(MainActivity.this,"长时间按下了按钮",   
						     Toast.LENGTH_LONG  
						     ).show();
					
					if(core.setFlag(i % ycount, i/ xcount)){
						Btn[ i].setBackgroundResource(R.drawable.ic_launcher);
					}else{
						Btn[ i].setBackgroundColor(color.background_light);
					}
					return false;
				}
			});
		}
	}

	private void repaint(boolean showS) {
		for (int i = 0; i <core.data.length; i++) {
			for (int j = 0; j <core.data[i].length; j++) {
				if (showS) {
					Btn[i *xcount + j ]
							.setText(core.data[i][j].toInt()+"");
				} else {
					Btn[ i *xcount + j]
							.setText(core.mask[i][j] ?
                                    (
                                            core.data[i][j].toInt() == 0 ?
                                                    " " : core.data[i][j].toInt()+""
                                    )
                                    :
                                    (
                                            core.flag[i][j] ? "▛" : "█"
                                    ));
					if(core.sf[i][j]){
						
					}
				}
			}
		}
	}

}
