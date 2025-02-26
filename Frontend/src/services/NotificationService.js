import {useToast} from "vue-toastification";

class NotificationService{
    toast = useToast();


    notification(type,message){
        let messageType
        switch (type) {
            case 'success':
                messageType = this.toast.success
                break;
            case 'error':
                messageType = this.toast.error

                break;
            case 'info':
                messageType = this.toast.info
                break;
            case 'warning':
                messageType = this.toast.warning
        }
        messageType(message, {
            position: "top-center",
            timeout: 1805,
            closeOnClick: true,
            pauseOnFocusLoss: true,
            pauseOnHover: false,
            draggable: true,
            draggablePercent: 0.6,
            showCloseButtonOnHover: false,
            hideProgressBar: true,
            closeButton: false,
            icon: true,
            rtl: false
        });

    }

}

export default new NotificationService()