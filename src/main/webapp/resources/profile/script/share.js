function confirmEdit(){

    var sharedText = $('#shared-text').val();
    var circuitOwner = $('.circuit-owner').val();
    var circuitName = $('.circuit-name').val();
    var circuitTags = $('.circuit-tags').val();

    $.ajax({
                url: "/profile/submitEdit",
                method: "GET",
                data: profileShareObject(circuitName, circuitOwner, sharedText, circuitTags),
                contentType: "application/json",
                success: function(data){
                    console.log(data);
                    hideShareMenu();
                    return false;
                }
            });
}

function editShare(circuitInfo){
    var circuitInfoObject = JSON.parse(circuitInfo);

    $(".share-menu").addClass("show");
    var textField = $('#shared-text');
    textField.val(circuitInfoObject.pCircuitShared);
}

function hideShareMenu(){
    $(".share-menu").removeClass("show");
}

function sendData(pCircuitName, pCircuitOwner){
    $.ajax({
            url: "/profile/share",
            method: "GET",
            data: profileShareObject(pCircuitName, pCircuitOwner),
            contentType: "application/json",
            success: function(data){
                console.log(data);
                var circuitInfoObject = JSON.parse(data);

                $('.circuit-name').val(circuitInfoObject.pCircuitName);
                $('.circuit-owner').val(circuitInfoObject.pCircuitOwner);
                $('.circuit-tags').val(circuitInfoObject.pCircuitTags);

                editShare(data);
                return false;
            }
        });
}
function getCircuit(currSelected){

    var currRow = $(currSelected).closest("tr").attr("class");
    var currName = $("." + currRow).find(".circuitNames");
    var currOwner = $("." + currRow).find(".circuitOwners");

    sendData(currName.html(), currOwner.html());
}

function profileShareObject(pCircuitName, pCircuitOwner, pCircuitShared, pCircuitTags){
    return {circuitName:pCircuitName, circuitOwner:pCircuitOwner, circuitShared:pCircuitShared, circuitTags:pCircuitTags}
}

