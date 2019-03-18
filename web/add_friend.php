<?php

  $friend_login = $_POST["friend_login"];
  $my_login = $_POST["my_login"];
  $my_name = $_POST["my_name"];
  $my_surname = $_POST["my_surname"];

  $JSONobj->status = "no_user";
  $JSONobj->sent = false;

  $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
  $result = pg_query($link,
                      "SELECT login, name, surname
                      FROM users
                      WHERE login = "'" . pg_escape_string($friend_login) . "'");
  $count = pg_num_rows($result);

  if ($count == 1) {
    $row = pg_fetch_array($result, 0);
    $friend_name = $result["name"];
    $friend_surname = $result["surname"];
    $result = pg_query($link,
                        "SELECT inviter, invitee, status
                        FROM friends
                        WHERE inviter = '" . pg_escape_string($my_login) . "'
                        AND invitee = '" . pg_escape_string($friend_login) . "'")
    $count = pg_num_rows($result);

    if ($count = 1) {
      $row = pg_fetch_array($result, 0);
      $status = $row["status"];
      if ($status == 0) {
        $JSONobj->status = "already_sent";
      }
      else {
        $JSONobj->status = "already_friends";
      }
    }
    else {
      $result = pg_query($link,
                          "SELECT inviter, invitee, status
                          FROM friends
                          WHERE inviter = '" . pg_escape_string($friend_login) . "'
                          AND invitee = '" . pg_escape_string($my_login) . "'")
      $count = pg_num_rows($result);

      if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $status = $row["status"];
        if ($status == 0) {
          $JSONobj->status = "pending_inivitation";
        }
        else {
          $JSONobj->status = "already_friends";
        }
      }
      else {
        $result = pg_query($link,
                    "UPDATE friends
                    SET inviter = '" . pg_escape_string($my_login) . "'
                    , invitee = '" . pg_escape_string($friend_login) . "'
                    , status = 0");
        if ($result) {
          $JSONobj->sent = true;
          $subject = "Nowe zaproszenie do znajomych w BUWing";

          $headers = "From: noreply@buwing.com\r\n";
          $headers .= "MIME-Version: 1.0\r\n";
          $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

          $message = '<p>' . $friend_name . ' ' . $friend_surname . ', użytkownik ' . $my_name . ' ' . $my_surname . ' wysłał Ci zaproszenie do znajomych w BUWing.</p>
                     <p>Potwierdź zaproszenie w aplikacji mobilnej.</p>';

          mail($newEmail, $subject, $message, $headers);
        }
      }
    }
  }

  pg_close($link);

  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
