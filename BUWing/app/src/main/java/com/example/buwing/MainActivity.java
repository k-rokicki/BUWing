package com.example.buwing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    Button loginButton, registerButton;
    EditText loginTextView, passwordTextView;
    static String name;
    static String surname;
    static String login;
    static String password;
    static String loginCredentialsFilename;
    static String loginCredentialsPath;
    static File loginCredentials;

    @SuppressLint("StaticFieldLeak")
    private class AuthenticationTask extends AsyncTask<Void, Void, Boolean> {
        private StringBuilder stringBuilder = new StringBuilder();
        private JSONObject obj;
        private boolean loggedIn = false;

        AuthenticationTask(){}

        @Override
        protected Boolean doInBackground(Void... voids) {
            stringBuilder.append("http://students.mimuw.edu.pl/~kr394714/buwing/login.php?login=");
            stringBuilder.append(login);
            stringBuilder.append("&password=");
            stringBuilder.append(password);
            String loginURL = stringBuilder.toString();
            StringBuilder response = new StringBuilder();

            HttpURLConnection conn = null;

            try {
                URL url = new URL(loginURL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(false);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                int status = conn.getResponseCode();

                if (status != 200) {
                    throw new IOException("Post failed with error code " + status);
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                String result = response.toString();
                try {
                    obj = new JSONObject(result);
                    name = obj.get("name").toString();
                    surname = obj.get("surname").toString();
                    loggedIn = obj.get("loggedin").toString().equals("1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return loggedIn;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            checkLoginSuccess(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginCredentialsFilename = "login_credentials.xml";
        loginCredentialsPath = getBaseContext().getFilesDir().getAbsolutePath() + "/" + loginCredentialsFilename;
        loginCredentials = new File(loginCredentialsPath);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
        passwordTextView = findViewById(R.id.passwordTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginTextView.getText().toString();
                password = passwordTextView.getText().toString();
                AuthenticationTask loginTask = new AuthenticationTask();
                loginTask.execute();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        if (loginCredentials.exists()) {
            XmlPullParserFactory parserFactory;
            try {
                parserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserFactory.newPullParser();
                InputStream inputStream = new FileInputStream(loginCredentials);
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(inputStream, null);
                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String eltName = null;

                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            eltName = parser.getName();

                            if ("login".equals(eltName)) {
                                login = parser.nextText();
                            } else if ("password".equals(eltName)) {
                                password = parser.nextText();
                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthenticationTask loginTask = new AuthenticationTask();
            loginTask.execute();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkLoginSuccess(boolean loggedIn) {
        if (loggedIn) {
            try {
                saveLoginCredentials(getBaseContext());
            } catch (IOException e) {
                if (loginCredentials.exists()) {
                    loginCredentials.delete();
                }
                e.printStackTrace();
            }
            Intent intent = new Intent(this, LoggedActivity.class);
            startActivity(intent);
        } else {
            if (loginCredentials.exists()) {
                loginCredentials.delete();
            }
            Toast.makeText(getApplicationContext(), "Błąd logowania", Toast.LENGTH_LONG).show();
        }
    }

    public static void saveLoginCredentials(Context context) throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag("", "credentials");
        xmlSerializer.startTag("", "login");
        xmlSerializer.text(login);
        xmlSerializer.endTag("", "login");
        xmlSerializer.startTag("", "password");
        xmlSerializer.text(password);
        xmlSerializer.endTag("", "password");
        xmlSerializer.endTag("", "credentials");
        xmlSerializer.endDocument();
        String content = writer.toString();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter
                (context.openFileOutput(loginCredentialsFilename, Context.MODE_PRIVATE));
        outputStreamWriter.write(content);
        outputStreamWriter.close();
    }

}
