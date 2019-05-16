<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="style.css">
        <title>BUWing - aktywowanie konta</title>
        <meta charset="UTF-8">
        <meta name="author" content="Kacper Rokicki">
    </head>

<body>

<style> body {text-align:center;} </style>
<br><br><br>
<h2>BUWing - aktywowanie konta</h2>
<br><br><br>

<?php
    $ini = parse_ini_file("database_credentials.ini");

    $userid = $_GET["userid"];
    $token = $_GET["token"];
    
    $link = pg_connect("host=labdb dbname=bd user=" . $ini["db_user"] . " password=" . $ini["db_password"]);
    $activated = false;
    
    $result = pg_query_params($link,
                                    "SELECT * FROM activate_account($1, $2)",
                                    array($userid, $token));

    if ($result) {
        $row = pg_fetch_array($result, 0);
        $activated = $row["success"];

        if ($activated) {
            $name = $row["name"];
            $surname = $row["surname"];
            $email = $row["email"];

            $subject = "Witamy w BUWing";

            $headers = "From: noreply@buwing.com\r\n";
            $headers .= "MIME-Version: 1.0\r\n";
            $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

            $message = "<p>" . $name . " " . $surname . ", dziękujemy za aktywację konta w BUWing.</p>
                        <p>Możesz już zalogować się do aplikacji. Zapraszamy!</p>";

            mail($email, $subject, $message, $headers);
        }
    }

    pg_close($link);

    if ($activated) {
        echo "Pomyślnie aktywowano konto.";
    } else {
        echo "Wystąpił błąd. Spróbuj ponownie.";
    }
?>

</body>
</html>