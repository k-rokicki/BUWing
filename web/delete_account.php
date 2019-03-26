<?php

    function randomString($length) {
        $str = "";
        $characters = array_merge(range('A','Z'), range('a','z'), range('0','9'));
        $max = count($characters) - 1;
        for ($i = 0; $i < $length; $i++) {
            $rand = mt_rand(0, $max);
            $str .= $characters[$rand];
        }
        return $str;
    }

    $login = $_POST["login"];
    $password = $_POST["password"];

    $JSONobj->deleted = 0;

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = pg_query($link,
                        "SELECT id, name, surname, email, password
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $userid = $row["id"];
        $name = $row["name"];
        $surname = $row["surname"];
        $email = $row["email"];
        $hashedPassword = $row["password"];
        
        if (password_verify($password, $hashedPassword)) {
            $result = pg_query($link,
                        "SELECT COUNT(*)
                        FROM pendingAccountDeletions
                        WHERE userid = " . $userid);
            $row = pg_fetch_array($result, 0);
            $alreadyPending = $row[0];

            $token = randomString(50);
            
            if ($alreadyPending == 0) {
                $result = pg_query($link,
                            "INSERT INTO pendingAccountDeletions VALUES (" .
                            $userid . ", '" .
                            $token . "')");
            } else {
                $result = pg_query($link,
                            "UPDATE pendingAccountDeletions
                            SET token = '" . $token .
                            "' WHERE userid = " . $userid);
            }

            if ($result) {
                $JSONobj->deleted = 1;

                $confirmationLink = "http://students.mimuw.edu.pl/~kr394714/buwing/confirm_account_deletion.php?userid="
                                        . $userid . "&token=" . $token;

                $subject = "Próba usunięcia konta w BUWing";

                $headers = "From: noreply@buwing.com\r\n";
                $headers .= "MIME-Version: 1.0\r\n";
                $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

                $message = '<p>' . $name . ' ' . $surname . ', odnotowaliśmy próbę usunięcia Twojego konta w BUWing.</p>
                            <p>Aby potwierdzić chęć usunięcia konta, kliknij w ten <a href="' . $confirmationLink . '">link</a></p>';

                mail($email, $subject, $message, $headers);
            }
        } else {
            $JSONobj->deleted = -1;
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>