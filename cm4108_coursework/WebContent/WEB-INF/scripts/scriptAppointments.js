$(document).ready(() => {
    const SELECTED = "selected";
    let todaysDate = getTodaysDate();

    $(".date").each(function(index){
        console.log(this);
       this.value = todaysDate;
    });

    $("tr").click((e) => {
        if(e.currentTarget.classList.contains(SELECTED)) {
            e.currentTarget.classList.remove(SELECTED)
        } else {
            e.currentTarget.classList.add(SELECTED);
        }
    });
});

function getTodaysDate() {
    let today = new Date();
    let dd = String(today.getDate()).padStart(2, '0');
    let mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
    let yyyy = today.getFullYear();
    return yyyy + '-' + mm + '-' + dd;
}