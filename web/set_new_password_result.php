<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="style.css">
        <title>BUWing - resetowanie hasła</title>
        <meta charset="UTF-8">
        <meta name="author" content="Kacper Rokicki">
    </head>

<body>

<style> body {text-align:center;} </style>
<br><br><br>
<h2>BUWing - resetowanie hasła</h2>
<br><br><br>

<?php

    $userid = $_POST["userid"];
    $newPassword = $_POST["newPassword"];
    $newPasswordRepeat = $_POST["newPasswordRepeat"];

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");

    if (isset($userid) && isset($newPassword) && isset($newPasswordRepeat) && 
        trim($userid) != "" && trim($newPassword) != "" && trim($newPasswordRepeat) != "") {

        if ($newPassword != $newPasswordRepeat) {
            echo "Wpisane hasła nie są takie same. Spróbuj ponownie.";
        } else {        
            $result = pg_query($link,
                                "DELETE
                                FROM resetPasswordTokens
                                WHERE userid = " . pg_escape_string($userid));

            if ($result) {
                $result = pg_query($link,
                                        "UPDATE users
                                        SET password = '" . password_hash($newPassword, PASSWORD_DEFAULT) .
                                        "' WHERE id = " . $userid .
                                        " RETURNING name, surname, email");

                if ($result) {
                    $row = pg_fetch_array($result, 0);
                    $name = $row["name"];
                    $surname = $row["surname"];
                    $email = $row["email"];

                    echo "Ustawiono nowe hasło.<br>Wysłaliśmy maila z potwierdzeniem.";

                    $subject = "Ustawiono nowe hasło w BUWing";

                    $headers = "From: noreply@buwing.com\r\n";
                    $headers .= "MIME-Version: 1.0\r\n";
                    $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

                    $message = '<p>' . $name . ' ' . $surname . ', potwierdzamy ustawienie nowego hasła do konta w BUWing.</p>
                                <p>Możesz ponownie zalogować się do aplikacji. Zapraszamy!</p>';

                    mail($email, $subject, $message, $headers);
                } else {
                    echo "Wystąpił błąd. Spróbuj ponownie.";
                }
            } else {
                echo "Wystąpił błąd. Spróbuj ponownie.";
            }
        }
    } else {
        echo "Nie wypełniono wszystkich pól. Spróbuj ponownie.";
    }

    pg_close($link);

?>

</body>
</html>