package com.sasisa.chat.sasisachat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;
import com.sasisa.chat.sasisachat.userinfo.PlusReputationFragment;

import cz.msebera.android.httpclient.Header;

public class UserInfoActivity extends SasisaAppCompatActivity {
    private String mUserID;
    private PlusReputationFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Bundle b = getIntent().getExtras();
        mUserID = b.getString("userid");
        setTitle(b.getString("username"));

        fragment = null;

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Button dialogButton = (Button) findViewById(R.id.show_user_dialog_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, ViewDialogActivity.class);
                intent.putExtra("sel", mUserID);
                startActivity(intent);
            }
        });

        if (mUserID.equals("216036")) {
            //Медаль Вседозволенности
            String page = "ID: <b>Ghostsilen</b><br/>\n" +
                    "<img src=\"http://wap.sasisa.ru/chat/inv.gif\" alt=\"inv\"/> <b>Время захода неизвестно</b><br/>" +
                    "Имя: <b>Ghostsilen</b><br/>\n" +
                    "Репутация: &#8734;<br/>" +
                    "Статус в чате: <b>Ghostsilen</b> <br/>\n" +
                    "Статус на форуме: <b>Ghostsilen</b> \n" +
                    "<br/>День рождения: <b>12 Апреля &#8734; г.</b><br/>\n" +
                    "Пол: <b>Mужчина</b><br/>\n" +
                    "Город: <b>Ghostsilen</b> (Ghostsilen)<br/>\n" +
                    "Занятость: <b>Ghostsilen</b><br/>" +
                    "Оператор: <b>Ghostsilen</b><br/>\n" +
                    "Модель мобилки: <b>Ghostsilen</b><br/>\n" +
                    "О себе: Всегда действовать наикратчайшим путем. Прямо и грубо. Ни в коем случае не учитывая последствий. Чем эти последствия тяжелее, тем лучше для дела. Потому что только безвыходная ситуация оправдывает подобную логику борьбы и противостояния.<br/>\n" +
                    "Постов в чате: <b>&#8734; (&#8734; сегодня)</b><br/>\n" +
                    "Постов на форуме: <b>&#8734;</b><br/>\n" +
                    "Ответы: <b>&#8734;</b><br/>\n" +
                    "Очков в мафии: <b>&#8734;</b><br/>\n" +
                    "Игровой баланс: <b>&#8734;</b><br/>\n" +
                    "Нарушений в чате: <b>Медаль Вседозволенности</b> <br/>" +
                    "Нарушений на форуме: <b>Медаль Вседозволенности</b><br />" +
                    "Дата регистрации: <b>С создания</b>";
            page += "<style>body {background-color: #AEEF4D}</style>";
            WebView wv = (WebView) findViewById(R.id.userinfo_webview);
            wv.loadDataWithBaseURL("", Parser.replaceALinks(Parser.replaceImageLinks(page)), "text/html", "UTF-8", "");
            dialogButton.setVisibility(View.VISIBLE);
        } else {
            final ProgressDialog pd = new ProgressDialog(UserInfoActivity.this);
            pd.setMessage("Загрузка данных...");
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    pd.dismiss();
                    UserInfoActivity.this.finish();
                }
            });
            pd.show();
            SasisaRestClient.getInstance().dwnloadUserInfoPage(mUserID, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    pd.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    String page = Parser.parseUserInfoPage(responseString);
                    page += "<style>body {background-color: #DFFFB0}</style>";
                    WebView wv = (WebView) findViewById(R.id.userinfo_webview);
                    wv.loadDataWithBaseURL("", Parser.replaceALinks(Parser.replaceImageLinks(page)), "text/html", "UTF-8", "");
                    wv.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                            if (url != null && url.startsWith("http://")) {
                                if (url.contains("&plus=1")) {
                                    fragment = PlusReputationFragment.newInstance(url);
                                    findViewById(R.id.blackbackgroundfragmentlayout).setVisibility(FrameLayout.VISIBLE);
                                    findViewById(R.id.fragmentplacelayout).setVisibility(FrameLayout.VISIBLE);
                                    getSupportFragmentManager().beginTransaction().
                                            add(R.id.fragmentplacelayout, fragment, "plusreputation").commit();
                                } else {
                                    new AlertDialog.Builder(UserInfoActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Открыть ссылку в браузере?")
                                            .setMessage("Хотите открыть ссылку в браузере?")
                                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    view.getContext().startActivity(
                                                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                                }

                                            })
                                            .setNegativeButton("Нет", null)
                                            .show();
                                }
                            }
                            return true;
                        }
                    });
                    pd.dismiss();
                    dialogButton.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideFragment() {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        fragment = null;
        findViewById(R.id.blackbackgroundfragmentlayout).setVisibility(FrameLayout.GONE);
        findViewById(R.id.fragmentplacelayout).setVisibility(FrameLayout.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (fragment != null) {
                hideFragment();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void refresh() {
        finish();
        startActivity(getIntent());
    }
}
