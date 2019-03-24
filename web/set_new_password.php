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

    $userid = $_GET["userid"];
    $token = $_GET["token"];
    
    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    
    if (isset($userid) && isset($token) &&
        trim($userid) != "" && trim($token) != "") {
        
        $result = pg_query($link,
                                "SELECT token
                                FROM resetPasswordTokens
                                WHERE userid = " . pg_escape_string($userid));

        $count = pg_num_rows($result);

        if ($count == 0) {
            echo "Wystąpił błąd. Spróbuj ponownie.";
        } else {
            $row = pg_fetch_array($result, 0);
            $activeToken = $row["token"];
            
            if ($activeToken != $token) {
                echo "Ten link nie jest aktywny.";
            } else {
                echo "<form action=\"set_new_password_result.php\" method=\"post\">\n";
                    echo "<input type=hidden name=userid value=" . $userid . ">";
                    echo "Wpisz nowe hasło: <input type=password name=newPassword><br><br>";
                    echo "Powtórz nowe hasło: <input type=password name=newPasswordRepeat><br><br>";
                    echo "<input type=submit value=\"Ustaw hasło\">\n";
                echo "</form>";
                echo "<br>\n";
            }
        }
    }

    pg_close($link);

?>

</body>
</html>