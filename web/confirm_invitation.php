<?php
  $ini = parse_ini_file("database_credentials.ini");

  $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);

  $myLogin = $_POST["myLogin"];
  $inviterLogin = $_POST["inviterLogin"];
  $password = $_POST["password"];

  $JSONobj->success = false;

  $result = pg_query($link, "SELECT id, password FROM users
                            WHERE login =  '" . pg_escape_string($myLogin) . "'");

  if (pg_num_rows($result) == 1) {
    $row = pg_fetch_array($result, 0);
    $myId = $row["id"];
    $hashedPassword = $row["password"];

    if (password_verify($password, $hashedPassword)) {
      $result = pg_query($link, "SELECT id FROM users
                                WHERE login =  '" . pg_escape_string($inviterLogin) . "'");

      $row = pg_fetch_array($result, 0);
      $inviterId = $row["id"];

      $result = pg_query($link, "UPDATE Friends SET status = 't'
                                WHERE inviterid = '" . pg_escape_string($inviterId) . "'
                                AND inviteeid = '" . pg_escape_string($myId) . "' AND status = 'f'");

      if ($result) {
        $JSONobj->success = true;
      }
    }
  }

  pg_close($link);
  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
