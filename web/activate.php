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

    $userid = $_GET["userid"];
    $token = $_GET["token"];
    
    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $activated = false;
    
    if (isset($userid) && isset($token) &&
        trim($userid) != "" && trim($token)) {
        
        $result = pg_query($link,
                                "SELECT COUNT(*)
                                FROM activationTokens
                                WHERE userid = " . pg_escape_string($userid) .
                                " AND token = '" . pg_escape_string($token) . "'");

        $row = pg_fetch_array($result, 0);
        $userExists = $row[0];
        
        if ($userExists == 1) {
            $result = pg_query($link,
                                "DELETE
                                FROM activationTokens
                                WHERE userid = " . pg_escape_string($userid));

            if ($result) {
                $result = pg_query($link,
                                        "UPDATE users
                                        SET activated = 1
                                        WHERE id = " . $userid .
                                        "RETURNING name, surname, email");

                if ($result) {
                    $row = pg_fetch_array($result, 0);
                    $name = $row["name"];
                    $surname = $row["surname"];
                    $email = $row["email"];

                    $activated = true;

                    $subject = "Witamy w BUWing";

                    $headers = "From: noreply@buwing.com\r\n";
                    $headers .= "MIME-Version: 1.0\r\n";
                    $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

                    $message = '<p>' . $name . ' ' . $surname . ', dziękujemy za aktywację konta w BUWing.</p>
                                <p>Możesz już zalogować się do aplikacji. Zapraszamy!</p>';

                    mail($email, $subject, $message, $headers);
                }
            }
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