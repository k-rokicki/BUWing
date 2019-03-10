<?php
    $dayOfWeek = date("N");
    $currentHour = date("G");
    $currentMinutes = date("i") * 1;
    
    if ($dayOfWeek > 1) {
        $prevDay = $dayOfWeek - 1;
    } else {
        $prevDay = 7;
    }
    
    $timetable = array(
        1 => array(
            'opensHour' => 8,
            'opensMinutes' => 0,
            'closesHour' => 22,
            'closesMinutes' => 0,
            'closesNextDay' => false,
        ),
        2 => array(
            'opensHour' => 8,
            'opensMinutes' => 0,
            'closesHour' => 22,
            'closesMinutes' => 0,
            'closesNextDay' => false,
        ),
        3 => array(
            'opensHour' => 8,
            'opensMinutes' => 0,
            'closesHour' => 22,
            'closesMinutes' => 0,
            'closesNextDay' => false,
        ),
        4 => array(
            'opensHour' => 8,
            'opensMinutes' => 0,
            'closesHour' => 22,
            'closesMinutes' => 0,
            'closesNextDay' => false,
        ),
        5 => array(
            'opensHour' => 8,
            'opensMinutes' => 0,
            'closesHour' => 22,
            'closesMinutes' => 0,
            'closesNextDay' => false,
        ),
        6 => array(
            'opensHour' => 9,
            'opensMinutes' => 0,
            'closesHour' => 21,
            'closesMinutes' => 0,
            'closesNextDay' => false,
        ),
        7 => array(
            'opensHour' => 15,
            'opensMinutes' => 0,
            'closesHour' => 20,
            'closesMinutes' => 0,
            'closesNextDay' => false,
        ),
    );

    $closesNextDay = $timetable[$prevDay]['closesNextDay'];
    $dayToFetchHours;

    if (!$closesNextDay) {
        $dayToFetchHours = $dayOfWeek;
    } else {
        if ($currentHour < $timetable[$prevDay]['closesHour'] ||
            ($currentHour == $timetable[$prevDay]['closesHour']  && $currentMinutes < $timetable[$prevDay]['closesMinutes'])) {
            $dayToFetchHours = $prevDay;
        } else {
            $dayToFetchHours = $dayOfWeek;
        }
    }
    
    $JSONobj->opensHour = $timetable[$dayToFetchHours]['opensHour'];;
    $JSONobj->opensMinutes = $timetable[$dayToFetchHours]['opensMinutes'];
    $JSONobj->closesHour = $timetable[$dayToFetchHours]['closesHour'];
    $JSONobj->closesMinutes = $timetable[$dayToFetchHours]['closesMinutes'];
    $JSONobj->closesNextDay = $timetable[$dayToFetchHours]['closesNextDay'];

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>