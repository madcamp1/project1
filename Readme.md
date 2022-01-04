# Project1_Readme

# 카이스트 몰입캠프 1주차 프로젝트

## 1. Project Description

---

카이스트 몰입캠프 1주차 프로젝트

## 2. Getting Started

---

### Gradle Version

- Gradle: 7.0.4

### SDK Version

- Android SDK Platform 32

### Dependency

```groovy
dependencies {
    implementation 'com.github.bumptech.glide:glide:4.11.0'
    implementation 'androidx.appcompat:appcompat:1.4.0'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.2'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.google.android.gms:play-services-location:19.0.0'
    implementation 'com.naver.maps:map-sdk:3.13.0'
    implementation 'com.google.android.material:material:1.1.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
```

## 3. Functionality & Implementation

---

### Project Structure

![projectStructure.png](Project1_Readme%206d00ec2adbb14a529616aa2db3d7f817/projectStructure.png)

- The application consists of three functionality
- Each function is served by TabActivity as fragment

### Contacts

---

![Contact.drawio (2).png](Project1_Readme%206d00ec2adbb14a529616aa2db3d7f817/Contact.drawio_(2).png)

- 연락처 정보는 ContentsPrvider에서 제공하는 ContactsContract 데이터베이스를 이용했습니다.
- ContactsContract DB의 각 테이블에서 어플리케이션 상의 연락처에 보여줄 정보들을 ContactsData 객체의 필드에 할당하고, 이를 ContactsAdapter상에서 ArrayList로 관리 및 사용했습니다.
    
    ```java
    //ContactData.java
    
    public class ContactData {
        private long portraitSrc, contact_id;
        private String name, phoneNum, description;
        public ContactData(){};
        public ContactData(long portraitSrc, String name, String phoneNum, String description, long contact_id) {
            this.portraitSrc = portraitSrc;
            this.name = name;
            this.phoneNum = phoneNum;
            this.description = description;
            this.contact_id = contact_id;
        }
    
        //..Getter & Setter
    }
    ```
    
    ```java
    //ContactAdapter.java
    
    public ArrayList<ContactData> getContactData(String input) {
          Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; //android provider 에서 제공하는 데이터 식별자
          //ContactsContract.Contacts - Constants for the Contact table
          // == 동일한 사람을 나타내는 연락처 집계당 하나의 레코드가 되는 연락처 테이블
          //ContactsContract.CommonDataKinds = ContactsContract.Data 테이블의 common data type 을 정의
          String[] qr = new String[]{"...queries"};
          //..NullException
    			String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
          ArrayList<ContactData> result;
    			//Set Where Clause
          String where = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + input + "%'" + " OR " + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE '%" + input + "%'";
          if (input.matches("[+-]?\\d*(\\.\\d+)?")) {
    					//'-'없는 숫자 쿼리 핸들링
              where += handleAdditionalQuery(input);
          }
          try (Cursor cursor = context.getContentResolver().query(uri, qr, where, null, sortOrder)) {
              //SELECT qr FROM ContactsContract.CommonDataKinds.Phone DESC/ASC ~~ 같은 느낌이라 보면 될 듯
              result = new ArrayList<ContactData>();
              if (cursor.moveToFirst()) {
                  do {
                      //...Set datas to ContactData object
                  } while (cursor.moveToNext());
              }
          } catch (Exception e) {
              e.printStackTrace();
              result = null;
          }
          return result;
      }
    ```
    
- 내부 연락처에서 정보를 가져와 Adapter상의 ContactData list에 불러오는 과정은 JVM에 별도로 Thread를 할당한 뒤 결과값을 Handler로 받아오도록 구현했습니다.
    
    ```java
    //ContactAdapter.java
    
    Handler hd = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    contactDatas = (ArrayList<ContactData>) msg.obj;
                    notifyDataSetChanged();
                }
    };
    new Thread(){
        @Override
        public void run() {
            super.run();
            contactDatas = getContactData(retrieve);
            Message msg = hd.obtainMessage(1, contactDatas);
            hd.sendMessage(msg);
        }
    }.start();
    ```
    
- Contact Fragment — Listener
    1. 상단의 검색창 텍스트 변경 시 retrieve함수를 호출하여 실시간으로 검색 및 출력합니다.
        
        ```java
        //Contact.java
        
        public void afterTextChanged(Editable editable) {
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contacts);
            ContactAdapter contactAdapter = (ContactAdapter) recyclerView.getAdapter();
            assert contactAdapter != null;
            contactAdapter.retrieveContact(editable.toString());
        }
        ```
        
    2. 하단의 추가 버튼 클릭 시 연락처 추가 Activity로 이동합니다.
        
        ```java
        //Contact.java
        
        public void onClick(View view) {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            requireContext().startActivity(intent);
        }
        ```
        
- Recyclerview — Listener
    1. ItemView를 길게 클릭 시 연락처 수정 및 삭제에 대한 Dialog 호출합니다. 선택에 따라 해당하는 동작 수행합니다.
        
        ```java
        //ContactAdapter.java
        
        @Override
        public boolean onLongClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(putOrDeleteMenu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int menuPosition) {
                    if (menuPosition == 0) {
                        context.startActivity(new Intent(Intent.ACTION_EDIT, Uri.parse(ContactsContract.Contacts.CONTENT_URI + "/" + Long.toString(indivContact.getContact_id()))));
                        notifyItemChanged(position);
                    }
                    else if (menuPosition == 1) {
                        contactDatas.remove(position); //db뿐만 아니라 recyclerview 내의 데이터도 삭제해줘야 함
                        notifyItemRemoved(position);
                    }
                }
            });
            builder.setNegativeButton("취소", null);
            builder.show();
            return true;
        }
        ```
        
    2. Swipe event의 경우 SwipeController 클래스를 추가적으로 구현한 뒤 Recyclerview에 붙여서 처리했습니다. SwipeController 는 ItemtouchHelper.Callback 클래스를 상속한 클래스이며 swipe관련 method를 오버라이딩하여 사용하는 Class입니다.
        
        ```java
        //SwipeController.java
        
        @Override
            public void onChildDraw(Canvas c,
                                    RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                if (actionState == ACTION_STATE_SWIPE) {
                    drawButtons(c, viewHolder, (int)dX);
                    setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive); //여기조건걸
                    if (dX >= buttonWidth|| dX <= -buttonWidth){
                        return;
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        ```
        
        Swipe event를 통한 변위에 따라 메시지, 통화에 대한 안내문이 출력됩니다. 손가락을 뗐을 때에는 진동과 함께 통화 혹은 메시지에 대한 activity로 넘어갑니다.
        
        ```java
        //SwipeController.java
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
            if (swipeBack && !currentTaskon) {
                currentTaskon = true;
                if (buttonWidth < dX){
                    call(viewHolder);
                    activateVibrator();
                    recyclerView.getChildAt(viewHolder.getAdapterPosition()).setX(buttonWidth);
                }
                else if (-buttonWidth > dX){
                    message(viewHolder);
                    activateVibrator();
                    recyclerView.getChildAt(viewHolder.getAdapterPosition()).setX(-buttonWidth);
                }
                currentTaskon = false;
            }
            return false;
        }
        ```
        

### Gallery

### Map

## 4. Implementation Detail

---

리사이징 거침

SwipeController 는 ItemtouchHelper.Callback 클래스를 상속하여 swipe관련 method 오버라이딩