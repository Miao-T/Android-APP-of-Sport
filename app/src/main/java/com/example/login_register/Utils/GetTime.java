package com.example.login_register.Utils;


public class GetTime {
    /**
    * 方法1
     **/
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//        Date date = new Date(System.currentTimeMillis());
//        mTvTime.setText(simpleDateFormat.format(date));

    /**
     * 方法2
     **/
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH)+1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        int second = calendar.get(Calendar.SECOND);
//        mTvTime.setText(year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second);
//        handler.postDelayed(runnable,1000);

    /**
     * 方法3
     **/
    //new TimeThread().start();

//    public class TimeThread extends Thread{
//        @Override
//        public void run() {
//            super.run();
//            do{
//                try{
//                    Thread.sleep(1000);
//                    Message msg = new Message();
//                    msg.what = 1;
//                    handler.sendMessage(msg);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//            }while(true);
//        }
//    }
//
//    private Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(@NonNull Message msg) {
//            switch (msg.what){
//                case 1:
//                    Calendar calendar = Calendar.getInstance();
//                    int year = calendar.get(Calendar.YEAR);
//                    int month = calendar.get(Calendar.MONTH)+1;
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
//                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                    int minute = calendar.get(Calendar.MINUTE);
//                    int second = calendar.get(Calendar.SECOND);
////                    if(hour == 23 && minute == 32 && second == 0){
////                        mTvTime.setText("succeed");
////                    }
//                    mTvTime.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
//                    break;
//            }
//            return false;
//        }
//    });
}
