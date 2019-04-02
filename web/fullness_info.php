<?php
    $ini = parse_ini_file("database_credentials.ini");

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
    
    $result = pg_query($link,
                        "SELECT COUNT(*)
                        FROM tables
                        WHERE taken = FALSE");
    
    $row = pg_fetch_array($result, 0);
    $freeSeatsCount = $row[0];

    $result = pg_query($link,
                        "SELECT COUNT(*)
                        FROM tables");

    $row = pg_fetch_array($result, 0);
    $allSeatsCount = $row[0];

    pg_close($link);

    $JSONobj->freeSeatsCount = $freeSeatsCount;
    $JSONobj->allSeatsCount = $allSeatsCount;

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>