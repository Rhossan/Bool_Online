function deleteProfileObject(pCircuitName, pCircuitOwner, pCurrRow){

    $.ajax({
            url: "/profile/delete",
            method: "GET",
            data: profileObject(pCircuitName, pCircuitOwner, pCurrRow),
            contentType: "application/json",
            success: function(data){
                console.log(data);
                console.log("hello");
                updateTable(data)
                return false;
            }
        });


}
function updateTable(pCurrRow){
    if (pCurrRow == null || pCurrRow == ""){

    }else{
       $("." + pCurrRow).remove();
    }
}

function deleteProfileRow(currSelected){

    console.log(currSelected);



    var currRow = $(currSelected).closest("tr").attr("class");
    var currName = $("." + currRow).find(".circuitNames");
    var currOwner = $("." + currRow).find(".circuitOwners");

    console.log(currName);
    console.log(currOwner);
    console.log(currRow);

    console.log(currName.html());
    console.log(currOwner.html());

    if (confirm("Delete Circuit?")){
        deleteProfileObject(currName.html(), currOwner.html(), currRow);
    }






}

function profileObject(pCircuitName, pCircuitOwner, pCurrRow){
    return {circuitName:pCircuitName, circuitOwner:pCircuitOwner, currRow:pCurrRow}
}

$(".delete-button").mousedown(function(e){deleteProfileRow(this)});