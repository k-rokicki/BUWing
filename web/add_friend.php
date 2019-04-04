<?php

  $friendLogin = $_POST["friendLogin"];
  $myLogin = $_POST["myLogin"];
  $myName = $_POST["myName"];
  $mySurname = $_POST["mySurname"];

  $JSONobj->status = "no_user";
  $JSONobj->sent = false;

  $ini = parse_ini_file("database_credentials.ini");

  $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
  $result = pg_query($link, "SELECT id FROM users WHERE login = '" . pg_escape_string($myLogin) . "'");
  $row = pg_fetch_array($result, 0);
  $myId = $row["id"];
  $result = pg_query($link,
                      "SELECT id, name, surname, email
                      FROM users
                      WHERE login = '" . pg_escape_string($friendLogin) . "'");
  $count = pg_num_rows($result);

  if ($count == 1) {
    $row = pg_fetch_array($result, 0);
    $friendId = $row["id"];
    $friendName = $row["name"];
    $friendSurname = $row["surname"];
    $friendEmail = $row["email"];
    if ($friendId == $myId) {
      $JSONobj->status = "myself";
    }
    else {
      $result = pg_query($link,
                          "SELECT inviterid, inviteeid, status
                          FROM friends
                          WHERE inviterid = '" . pg_escape_string($myId) . "'
                          AND inviteeid = '" . pg_escape_string($friendId) . "'");
      $count = pg_num_rows($result);

      if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $status = ($row["status"] == 't');
        if (!$status) {
          $JSONobj->status = "already_sent";
        }
        else {
          $JSONobj->status = "already_friends";
        }
      }
      else {
        $result = pg_query($link,
                            "SELECT inviterid, inviteeid, status
                            FROM friends
                            WHERE inviterid = '" . pg_escape_string($friendId) . "'
                            AND inviteeid = '" . pg_escape_string($myId) . "'");
        $count = pg_num_rows($result);

        if ($count == 1) {
          $row = pg_fetch_array($result, 0);
          $status = ($row["status"] == 't');
          if (!$status) {
            $JSONobj->status = "pending_invitation";
          }
          else {
            $JSONobj->status = "already_friends";
          }
        }
        else {
          $result = pg_query($link,
                      "INSERT INTO friends
                      VALUES ('" . pg_escape_string($myId) . "'
                      , '" . pg_escape_string($friendId) . "'
                      , default)");
          if ($result) {
            $JSONobj->sent = true;
            $subject = "Nowe zaproszenie do znajomych w BUWing";

            $headers = "From: noreply@buwing.com\r\n";
            $headers .= "MIME-Version: 1.0\r\n";
            $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

            $message = '<p>' . $friendName . ' ' . $friendSurname . ', użytkownik ' . $myName . ' ' . $mySurname . ' wysłał Ci zaproszenie do znajomych w BUWing.</p>
                       <p>Potwierdź zaproszenie w aplikacji mobilnej.</p>';

            mail($friendEmail, $subject, $message, $headers);
          }
        }
      }
    }
  }

  pg_close($link);

  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
