<!DOCTYPE html>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="style.css">
        <title>BUWing - usunięcie konta</title>
        <meta charset="UTF-8">
        <meta name="author" content="Kacper Rokicki">
    </head>

<body>

<style> body {text-align:center;} </style>
<br><br><br>
<h2>BUWing - usunięcie konta</h2>
<br><br><br>

<?php

    $userid = $_GET["userid"];
    $token = $_GET["token"];
    
    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $success = 0;
    
    if (isset($userid) && isset($token) &&
        trim($userid) != "" && trim($token)) {
        
        $result = pg_query($link,
                                "SELECT token
                                FROM pendingAccountDeletions
                                WHERE userid = " . pg_escape_string($userid));

        $count = pg_num_rows($result);

        if ($count != 0) {
            $row = pg_fetch_array($result, 0);
            $activeToken = $row["token"];
            
            if ($activeToken != $token) {
                $success = -1;
            } else {
                $result = pg_query($link,
                                    "DELETE
                                    FROM pendingAccountDeletions
                                    WHERE userid = " . pg_escape_string($userid));

                if ($result) {
                    $result = pg_query($link,
                                            "UPDATE tables
                                            SET taken = false, userid = null
                                            WHERE userid = " . $userid);
                    
                    if ($result) {                    
                        $result = pg_query($link,
                                                "DELETE FROM friends
                                                WHERE inviterid = " . $userid . "
                                                OR inviteeid = " . $userid);

                        if ($result) {
                            $result = pg_query($link,
                                                    "DELETE FROM users
                                                    WHERE id = " . $userid);

                            if ($result) {
                                $success = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    pg_close($link);

    if ($success == 1) {
        echo "Pomyślnie usunięto konto.";
    } elseif ($success == -1) {
        echo "Ten link nie jest aktywny.";
    } else {
        echo "Wystąpił błąd. Spróbuj ponownie.";
    }
    
?>

</body>
</html>