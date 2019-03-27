<?php
  $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");

  $myLogin = $_POST["myLogin"];
  $inviterLogin = $_POST["inviterLogin"];

  $result = pg_query($link, "SELECT id FROM users
                            WHERE login =  '" . pg_escape_string($myLogin) . "'");

  $row = pg_fetch_array($result, 0);
  $myId = $row["id"];

  $result = pg_query($link, "SELECT id FROM users
                            WHERE login =  '" . pg_escape_string($inviterLogin) . "'");

  $row = pg_fetch_array($result, 0);
  $inviterId = $row["id"];

  $result = pg_query($link, "UPDATE Friends SET status = 't'
                            WHERE inviterid = '" . pg_escape_string($inviterId) . "'
                            AND inviteeid = '" . pg_escape_string($myId) . "' AND status = 'f'");

  if ($result) {
    $JSONobj->success = true;
  } else {
    $JSONobj->success = false;
  }

  pg_close($link);
  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
