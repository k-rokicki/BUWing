<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="style.css">
        <title>BUWing - zmiana hasła</title>
        <meta charset="UTF-8">
        <meta name="author" content="Kacper Rokicki">
    </head>

<body>

<style> body {text-align:center;} </style>
<br><br><br>
<h2>BUWing - zmiana hasła</h2>
<br><br><br>

<?php

    $userid = $_GET["userid"];
    $token = $_GET["token"];
    
    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $activated = false;
    
    if (isset($userid) && isset($token) &&
        trim($userid) != "" && trim($token)) {
        
        $result = pg_query($link,
                                "SELECT password, token
                                FROM pendingPasswordChanges
                                WHERE userid = " . pg_escape_string($userid));

        $count = pg_num_rows($result);

        if ($count == 0) {
            echo "Wystąpił błąd. Spróbuj ponownie.";
        } else {
            $row = pg_fetch_array($result, 0);
            $newPassword = $row["password"];
            $activeToken = $row["token"];
            
            if ($activeToken != $token) {
                echo "Ten link nie jest aktywny.";
            } else {
                $result = pg_query($link,
                                    "DELETE
                                    FROM pendingPasswordChanges
                                    WHERE userid = " . pg_escape_string($userid));

                if ($result) {
                    $result = pg_query($link,
                                            "UPDATE users
                                            SET password = '" . $newPassword .
                                            "' WHERE id = " . $userid .
                                            " RETURNING name, surname, email");

                    if ($result) {
                        $row = pg_fetch_array($result, 0);
                        $name = $row["name"];
                        $surname = $row["surname"];
                        $email = $row["email"];

                        echo "Pomyślnie zmieniono hasła.";

                        $subject = "Zmiana hasła w BUWing";

                        $headers = "From: noreply@buwing.com\r\n";
                        $headers .= "MIME-Version: 1.0\r\n";
                        $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

                        $message = '<p>' . $name . ' ' . $surname . ', potwierdzamy zmianę hasła do konta w BUWing.</p>';

                        mail($email, $subject, $message, $headers);
                    } else {
                        echo "Wystąpił błąd. Spróbuj ponownie.";
                    }
                } else {
                    echo "Wystąpił błąd. Spróbuj ponownie.";
                }
            }
        }
    }

    pg_close($link);

?>

</body>
</html>