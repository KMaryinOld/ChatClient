package com.sasisa.chat.sasisachat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;
import com.sasisa.chat.sasisachat.dialog.DialogMessagesAdapter;
import com.sasisa.chat.sasisachat.dialog.NewMessageDialogFragment;
import com.sasisa.chat.sasisachat.dialog.UserDialogMessage;

import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class ViewDialogActivity extends SasisaAppCompatActivity {
    private String selID;
    private ListView mMessagesList;
    private NewMessageDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dialog);

        Bundle bundle = getIntent().getExtras();
        selID = bundle.getString("sel");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mMessagesList = (ListView) findViewById(R.id.dialog_messages_list);

        mMessagesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount > totalItemCount - 10) {

                }
            }
        });

        final ProgressDialog pd = new ProgressDialog(ViewDialogActivity.this);
        pd.setMessage("Загрузка данных...");
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                pd.dismiss();
                ViewDialogActivity.this.finish();
            }
        });
        SasisaRestClient.getInstance().dwnloadDialogUserPage(selID, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Vector<UserDialogMessage> messages = Parser.parseDialogMessages(responseString);
                DialogMessagesAdapter adapter = new DialogMessagesAdapter(ViewDialogActivity.this, messages);
                mMessagesList.setAdapter(adapter);

                pd.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_view_dialog, menu);
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

    public void onClickNewMessageButton(View v) {
        dialogFragment = NewMessageDialogFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.dialog_new_message_fragment_layout,
                dialogFragment, "newmessage").commit();
    }

    public void sendMessage(View v) {
        View fragmentView = dialogFragment.getView();
        final EditText editText = (EditText) fragmentView.findViewById(R.id.fragment_new_message_text);
        fragmentView.findViewById(R.id.fragment_new_message_send_message_button).setEnabled(false);
        fragmentView.findViewById(R.id.fragment_new_message_close_fragment).setEnabled(false);
        fragmentView.findViewById(R.id.send_dialog_message_progressbar).setVisibility(View.VISIBLE);
        SasisaRestClient.getInstance().sendDialogMessage(selID, editText.getText().toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                finish();
                startActivity(getIntent());
            }
        });
    }
}
