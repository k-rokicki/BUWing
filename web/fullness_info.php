<?php
    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    
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