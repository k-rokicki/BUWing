<?php
    $opensHour;
    $opensMinutes;
    $closesHour;
    $closesMinutes;

    $dayOfWeek = date("N");
    
    $opensMinutes = "00";
    $closesMinutes = "00";

    if ($dayOfWeek >= 1 && $dayOfWeek <= 5) {
        $opensHour = "8";
        $closesHour = "22";
    } elseif ($dayOfWeek == 6) {
        $opensHour = "9";
        $closesHour = "21";
    } elseif ($dayOfWeek == 7) {
        $opensHour = "15";
        $closesHour = "20";
    }

    $JSONobj->opensHour = $opensHour;
    $JSONobj->opensMinutes = $opensMinutes;
    $JSONobj->closesHour = $closesHour;
    $JSONobj->closesMinutes = $closesMinutes;

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>