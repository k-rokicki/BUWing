<?php
  $ini = parse_ini_file("database_credentials.ini");

  $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);

  $myLogin = $_POST["myLogin"];
  $friendLogin = $_POST["friendLogin"];

  $result = pg_query($link, "SELECT id FROM users
                            WHERE login =  '" . pg_escape_string($myLogin) . "'");

  $row = pg_fetch_array($result, 0);
  $myId = $row["id"];

  $result = pg_query($link, "SELECT id FROM users
                            WHERE login =  '" . pg_escape_string($friendLogin) . "'");

  $row = pg_fetch_array($result, 0);
  $friendId = $row["id"];

  $result = pg_query($link, "DELETE FROM Friends
                            WHERE (inviterid = " . pg_escape_string($myId) . "
                            AND inviteeid = " . pg_escape_string($friendId) . ")
                            OR (inviteeid = " . pg_escape_string($myId) . "
                            AND inviterid = " . pg_escape_string($friendId) . ")");

  if ($result) {
    $JSONobj->success = true;
  } else {
    $JSONobj->success = false;
  }

  pg_close($link);
  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
