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
    $ini = parse_ini_file("database_credentials.ini");

    $userid = $_POST["userid"];
    $token = $_POST["token"];
    $newPassword = $_POST["newPassword"];
    $newPasswordRepeat = $_POST["newPasswordRepeat"];

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);

    $result = pg_query($link,
                             "SELECT COUNT(*)
                             FROM resetPasswordTokens
                             WHERE userid = " . pg_escape_string($userid) .
                             " AND token = '" . pg_escape_string($token) . "'");

    $row = pg_fetch_array($result, 0);
    $correctToken = $row[0];
        
    if ($correctToken == 0) {
        echo "Ten link nie jest aktywny.";
    } else {
        if (isset($userid) && isset($newPassword) && isset($newPasswordRepeat) && 
            trim($userid) != "" && trim($newPassword) != "" && trim($newPasswordRepeat) != "") {

            if ($newPassword != $newPasswordRepeat) {
                echo "Hasła nie są takie same. Spróbuj ponownie.";
            } elseif (mb_strtolower($newPassword,'UTF-8') == $newPassword) {
                echo "Hasło musi zawierać wielką literę. Spróbuj ponownie.";
            } elseif (mb_strtoupper($newPassword,'UTF-8') == $newPassword) {
                echo "Hasło musi zawierać małą literę. Spróbuj ponownie.";
            } elseif (!preg_match('/[0-9]/', $newPassword)) {
                echo "Hasło musi zawierać cyfrę. Spróbuj ponownie.";
            } elseif (!preg_match('/[@$!%*?&,.;]/', $newPassword)) {
                echo "Hasło musi zawierać znak specjalny: @$!%*?&,.; <br>";
                echo "Spróbuj ponownie.";
            } elseif (mb_strlen($newPassword,'UTF-8') < 8) {
                echo "Hasło musi mieć co najmniej 8 znaków";
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
    }

    pg_close($link);

?>

</body>
</html>