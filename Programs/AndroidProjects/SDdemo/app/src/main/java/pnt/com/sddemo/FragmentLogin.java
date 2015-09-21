package pnt.com.sddemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pnt on 8/25/15.
 */
public class FragmentLogin extends Fragment {
    private Button bt_signIn;
    private Button bt_create;
    private EditText ed_mail;
    private EditText ed_pass;
    private String mail;
    private String pass;
    private CheckBox cb;
    static int check = 0;
    NetWork netWork = new NetWork();
    @Override
    public void onPause() {
        if (cb.isChecked()) {
            SharedPreferences pre = getActivity().getSharedPreferences("login",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pre.edit();
            editor.putString("mail", ed_mail.getText().toString());
            editor.putString("pass", ed_pass.getText().toString());
            editor.putBoolean("check", true);
            editor.commit();
        }
        else{
            SharedPreferences pre = getActivity().getSharedPreferences("login",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pre.edit();
            editor.putString("mail", "");
            editor.putString("pass", "");
            editor.putBoolean("check", false);
            editor.commit();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        SharedPreferences pre = getActivity().getSharedPreferences("login",
                Context.MODE_PRIVATE);
        ed_mail.setText(pre.getString("mail", ""));
        ed_pass.setText(pre.getString("pass", ""));
        cb.setChecked(pre.getBoolean("check", false));

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_login, null);
        bt_signIn = (Button) view.findViewById(R.id.but_signIn);
        bt_create = (Button) view.findViewById(R.id.but_createAccount);
        ed_mail = (EditText) view.findViewById(R.id.editText_mail);
        ed_pass = (EditText) view.findViewById(R.id.editText_pass);
        cb = (CheckBox) view.findViewById(R.id.checkBox);
        bt_signIn.setEnabled(false);
        ed_mail.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (ed_mail.getText().toString().equals("") == false
                        && ed_pass.getText().toString().equals("") == false) {
                    bt_signIn.setEnabled(true);
                    bt_signIn.setBackgroundResource(R.drawable.demo2);
                } else {
                    bt_signIn.setEnabled(false);
                    bt_signIn.setBackgroundResource(R.drawable.demo);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        ed_pass.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (ed_mail.getText().toString().equals("") == false
                        && ed_pass.getText().toString().equals("") == false) {
                    bt_signIn.setEnabled(true);
                    bt_signIn.setBackgroundResource(R.drawable.demo2);
                } else {
                    bt_signIn.setEnabled(false);
                    bt_signIn.setBackgroundResource(R.drawable.demo);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        ed_pass.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                ed_pass.setText("");
                return false;
            }
        });
        bt_signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mail = ed_mail.getText().toString();
                pass = ed_pass.getText().toString();
                netWork.setMail(mail);
                netWork.setPass(pass);
                if (!isValidEmail(mail)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());
                    builder.setTitle("Fail");
                    builder.setMessage("Mail incorrect @@");
                    builder.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.setCancelable(false);
                } else if (netWork.checkInternetConnect(getActivity())) {
                    check = 0;
                    NetWorkAsyncTask nw = (NetWorkAsyncTask) new NetWorkAsyncTask().execute("http://thachpn.name.vn/account/check_account.php");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Internet");
                    builder.setMessage("Có đéo mạng đâu @@");
                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bt_signIn.callOnClick();
                        }
                    });
                    builder.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.setCancelable(false);
                }
            }
        });
        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentCreate()).addToBackStack(null).commit();
            }
        });
        return view;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }

    }
    public class NetWorkAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog pb;
        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog(getActivity());
            pb.setMessage("Login...");
            pb.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if(pb!=null){
                pb.dismiss();
            }
            if(s!=null){
                check = netWork.checkAccount(s);
                if (check == 1) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    FragmentHome fragment = new FragmentHome();
                    ft.replace(R.id.container, fragment);
                    //ft.addToBackStack(null);
                    ft.commit();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Fail");
                    builder.setMessage("Invalid login or password @@");
                    builder.setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            HttpResponse response = null;
            try{
                response = netWork.makeRquest(url);
            }catch (IOException e){
                return  null;
            }
            if(response!=null){
                String content = null;
                try{
                    content = netWork.processHTTPResponce(response);
                    return content;
                }catch (IOException e){
                    return  null;
                } catch (ParseException e) {
                    return  null;
                }
            }
            return null;

        }
    }
}
