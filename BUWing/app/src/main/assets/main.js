var idStolik;
var initWindowHeight = window.innerHeight;
var initWindowWidth = window.innerWidth;

    document.body.onclick = function(e) {   //when the document body is clicked
        if (window.event) {
            e = event.srcElement;           //assign the element clicked to e (IE 6-8)
        }
        else {
           e = e.target;                   //assign the element clicked to e
        }

        if (!e.className || e.className.indexOf('popup') == -1) { //zamykanie popupow gdy kliknie sie gdzie indziej
            closePopups();
        }

        if (e.className && e.className.indexOf('stolik') != -1) {
            idStolik = e;
            if (e.status == "wolny") {  //obsluga wolnego
                showPopupTT(); 
            }
            else if (e.status == "okupowany") { //okupowany to stolik ktory zajmuje uzytkownik
                showPopupFT();
            }
        }
        
    }

    // pinch zoomowanie stolikow
    document.getElementById("biblioteka").addEventListener('touchend', touchendeventListener, false);

    function touchendeventListener(event) {
        var tables = document.getElementsByClassName('stolik');
            if (window.innerHeight < initWindowHeight * 0.7) { //na razie na poziomie 70%
                for (let i = 0; i < tables.length; i++) {
                    tables[i].style.display = "block";
                }
            }
            else {
                for (let i = 0; i < tables.length; i++) {
                    tables[i].style.display = "none";
                }
            }
    }
    
    

    function showPopupTT() {
        var windowHeight = window.innerHeight * 0.5; // zeby przy zoomie rozmiar popupu sie nie psul
        var windowWidth = window.innerWidth * 0.5;
        var font_size = window.innerHeight * 0.05;
        console.log(windowHeight);
        document.getElementById("popupZajmij").style.height = windowHeight + "px";
        document.getElementById("popupZajmij").style.width = windowWidth + "px";
        var popupButtons = document.getElementsByClassName('popupButton');
        for (let i = 0; i < popupButtons.length; i++) {
            popupButtons[i].style.fontSize = font_size + "px";
        }
        document.getElementById("popupZajmij").style.display = "block";
    }

    function showPopupFT() {
        var windowHeight = window.innerHeight * 0.5; 
        var font_size = window.innerHeight * 0.05;
        document.getElementById("popupZwolnij").style.height = windowHeight + "px";
        document.getElementById("popupZwolnij").style.width = windowHeight + "px";
        var popupButtons = document.getElementsByClassName('popupButton');
        for (let i = 0; i < popupButtons.length; i++) {
            popupButtons[i].style.fontSize = font_size + "px";
        }
        document.getElementById("popupZwolnij").style.display = "block";
    }

    function closePopups() {
        document.getElementById("popupZajmij").style.display = "none";
        document.getElementById("popupZwolnij").style.display = "none";
    }

    function takeTable() {
        if (successfulTT()) {
            idStolik.style.background = "orange";
            idStolik.status = "okupowany";
        }
        closePopups();
    }

    function freeTable() {
        if (successfulFT()) {
            idStolik.style.background = "#28724F";
            idStolik.status = "wolny";
        }
        closePopups();
    }

    //laczenie z baza czy sie udalo zajac
    function successfulTT() {
        return true;
    }
    //laczenie z baza czy udalo sie zwolnic
    function successfulFT() {
        return true;
    }

    //przypisze statusy stolikom na podstawie skryptu php przy zaladowaniu strony
    function assignTableStatus() {
        //wyluskanie loginu uzytkownika zeby przekazac phpowi
        var url_string = window.location.href;
        var url = new URL(url_string);
        var c = url.searchParams.get("login");
        
        document.getElementById("stolik1").status = "wolny";
        document.getElementById("stolik2").status = "wolny";

        document.getElementById("stolik3").status = "zajety";
        document.getElementById("stolik3").style.background = "#C86BA8";
    }