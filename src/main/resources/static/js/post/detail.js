function postLikeClick(id){
    if($(`#heart-${id}`).hasClass("my_fake_like")){
        postUnLike(id);
     } else {
        postLike(id);
     }
}

// 복잡해지니까 메서드로 나눈다.
function postLike(id){
    $(`#heart-${id}`).addClass("my_fake_like");
    $(`#heart-${id}`).removeClass("my_fake_un_like");
    $(`#heart-${id}`).removeClass("far");
    $(`#heart-${id}`).addClass("fa-solid");
}
function postUnLike(id){
    $(`#heart-${id}`).addClass("my_fake_un_like");
    $(`#heart-${id}`).removeClass("my_fake_like");
    $(`#heart-${id}`).removeClass("fa-solid");
    $(`#heart-${id}`).addClass("far");
}

// 게시글 삭제, 권한체크 후 삭제 /s/api/post/postId
$("#btn-delete").click(() => {
    postDelete();
});



// 게시글 삭제, 권한체크 후 삭제 /s/api/post/postId
$("#btn-delete").click(() => {
    postDelete();
});

let postDelete = async () => {

    let postId = $("#postId").val();
    let pageOwnerId = $("#pageOwnerId").val();

    let response = await fetch(`/s/api/post/${postId}`, {
        method: "DELETE"
    });

    if (response.status == 200) {
        alert("삭제성공");
        location.href = `/user/${pageOwnerId}/post`;
    } else {
        alert("삭제실패");
    }
}
