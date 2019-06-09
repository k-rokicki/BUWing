//window.scrollTo( 250, 250 );


// do sprawdzenia

var idStolik;
var initWindowHeight = window.innerHeight;
var initWindowWidth = window.innerWidth;
var login, password, floor, friend;







/* Funkcja wywoływana przy ładowaniu mapy */
function start() {

    getUserData();
    initTables();
    assignTableStatus();
}



/* Funkcja pobierająca i wypisująca dane użytownika */
function getUserData() {

    var url_string = window.location.href;
    var url = new URL(url_string);

    login = url.searchParams.get("login");
    password =  url.searchParams.get("password");
    floor = url.searchParams.get("floor");

    friend = url.searchParams.get("friend");
    if (friend != null) {
        setFriendOnMap();
    }
}


/* Funkcja znajduje znajomego i umieszcza jego znacznik na mapie */
function setFriendOnMap() {

    var postData = 'login=' + login + '&password=' + password + '&friend=' + friend;

        var http = new XMLHttpRequest();
        var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/friend_table_info.php';
        http.open('POST', url, true);

        http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        http.send(postData);

        http.onreadystatechange = function() {
            if (http.readyState == 4 && http.status == 200) {
                const content = JSON.parse(http.responseText);

                var result = content['tablesId'];

                if (result != null) {
                    setFriendsMarker(result);
                }
                else {
                 //   showPopup(document.getElementById("popupBrakZnajomego"));
                }
            }
        }
}



/* Fukncja przypisująca stolikom id */
function initTables() {

    var tables = document.getElementsByClassName('stolik');

    for (let i = 0; i < tables.length; i++) {
        tables[i].setAttribute("id", i);
    }
}



/* Funkcja ustawijąca znacznik nad ławką o danym id */
function setFriendsMarker(tablesId) {

    var svg = document.getElementsByTagName('svg')[1];
    var circle = document.getElementsByTagName('circle')[0];

    var t = document.getElementById(tablesId);
    var x = t.getAttribute('x');
    var y = t.getAttribute('y');

    circle.setAttribute("display", "block");
    svg.setAttribute("x", x);
    svg.setAttribute("y", y);
}



/* Obsługa kliknięć na poszczególne elementy ekranu */
document.body.onclick = function(e) {

    if (window.event)
        e = event.srcElement;
    else
       e = e.target;

    if (e.getAttribute('class') && e.getAttribute('class').indexOf('stolik') != -1) {
        idStolik = e;

        //obsluga wolnego
        if (e.status == "wolny") {
            var postData = 'login=' + login + '&password=' + password;
            var http = new XMLHttpRequest();
            var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/seat_taken.php';
            http.open('POST', url, true);

            http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            http.send(postData);

            http.onreadystatechange = function() {
                if (http.readyState == 4 && http.status == 200) {
                    const content = JSON.parse(http.responseText);
                    var result = content['taken'];

                    if (!result) {
                        Android.showPopupZajmij();
                    }
                    else {
                        idStolik = document.getElementById(content['seatId']);
                        Android.showPopupZwolnijPrev();
                    }
                }
            }
        }
        //okupowany to stolik ktory zajmuje uzytkownik
        else if (e.status == "okupowany") {
            Android.showPopupZwolnij();
        }
    }
}

/* Funkcja zwalniająca zajmowany przez użytkownika stolik */
function freeTable() {

    var postData = 'login=' + login + '&password=' + password;
    var http = new XMLHttpRequest();
    var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/release_seat.php';
    http.open('POST', url, true);

    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    http.send(postData);

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            const content = JSON.parse(http.responseText);

            var result =  content['released'];
            if (result && idStolik) {
                idStolik.setAttribute("fill", "#28724F");
                idStolik.status = "wolny";
                Android.releasedSuccess();
            }
            else {
                Android.tryAgain();
            }
        }
    }
}


/* Funkcja zajmująca wybrany przez użytkownika stolik */
function takeTable() {

    var table = idStolik.id;
    var postData = 'login=' + login + '&password=' + password + '&table=' + table + '&floor=' + floor;

    var http = new XMLHttpRequest();
    var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/take_seat.php';
    http.open('POST', url, true);

    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    http.send(postData);

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            const content = JSON.parse(http.responseText);

            var result = content['took'];
            if (result == 1) {
                idStolik.setAttribute("fill", "orange");
                idStolik.status = "okupowany";
                Android.takenSuccess();
            }
            else if (result == -1) {
                Android.takenFail();
            }
            else {
                Android.tryAgain();
            }
        }
    }
}



/* Funkcja przypisująca statusy stolikom na podstawie skryptu php przy zaladowaniu strony */
function assignTableStatus() {

    var postData = 'login=' + login + '&password=' + password + '&floor=' + floor;
    var http = new XMLHttpRequest();
    var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/tables_status_info.php';
    http.open('POST', url, true);

    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    http.send(postData);

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            const content = JSON.parse(http.responseText);
            var n = content['result'];
            var tables = content['table'];

            for (let i = 0; i < n; i++) {
                var table = document.getElementById(tables[i]["id"]);

                if (table != null) {
                    if (tables[i]["taken"] == "t") {
                        if (tables[i]["login"] == login) {
                            table.status = "okupowany";
                            table.setAttribute("fill", "orange");
                        }
                        else {
                            table.status = "zajety";
                            table.setAttribute("fill", "#C86BA8");
                        }
                    }
                    else {
                        table.status = "wolny";
                        table.setAttribute("fill", "#28724F");
                    }
                }
            }
        }
    }
}