# Readme

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

```
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

### Overall Structure

![https://www.notion.soProject1_Readme%206d00ec2adbb14a529616aa2db3d7f817/projectStructure.png](https://www.notion.soProject1_Readme%206d00ec2adbb14a529616aa2db3d7f817/projectStructure.png)

projectStructure.png

- The application consists of three functionality
- Each function is served by TabActivity as fragment

### Contacts

---

![https://www.notion.soProject1_Readme%206d00ec2adbb14a529616aa2db3d7f817/Contact.drawio_(2).png](https://www.notion.soProject1_Readme%206d00ec2adbb14a529616aa2db3d7f817/Contact.drawio_(2).png)

Contact.drawio (2).png

- 연락처 정보는 ContentsPrvider에서 제공하는 ContactsContract 데이터베이스를 이용했습니다.
- ContactsContract DB의 각 테이블에서 어플리케이션 상의 연락처에 보여줄 정보들을 ContactsData 객체의 필드에 할당하고, 이를 ContactsAdapter상에서 ArrayList로 관리 및 사용했습니다.
    
    ```
    //ContactData.javapublic class ContactData {    private long portraitSrc, contact_id;    private String name, phoneNum, description;    public ContactData(){};    public ContactData(long portraitSrc, String name, String phoneNum, String description, long contact_id) {        this.portraitSrc = portraitSrc;        this.name = name;        this.phoneNum = phoneNum;        this.description = description;        this.contact_id = contact_id;    }    //..Getter & Setter}
    ```
    
    ```
    //ContactAdapter.javapublic ArrayList<ContactData> getContactData(String input) {      Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; //android provider 에서 제공하는 데이터 식별자      //ContactsContract.Contacts - Constants for the Contact table      // == 동일한 사람을 나타내는 연락처 집계당 하나의 레코드가 되는 연락처 테이블      //ContactsContract.CommonDataKinds = ContactsContract.Data 테이블의 common data type 을 정의      String[] qr = new String[]{"...queries"};      //..NullException            String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;      ArrayList<ContactData> result;            //Set Where Clause      String where = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + input + "%'" + " OR " + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE '%" + input + "%'";      if (input.matches("[+-]?\\d*(\\.\\d+)?")) {                    //'-'없는 숫자 쿼리 핸들링          where += handleAdditionalQuery(input);      }      try (Cursor cursor = context.getContentResolver().query(uri, qr, where, null, sortOrder)) {          //SELECT qr FROM ContactsContract.CommonDataKinds.Phone DESC/ASC ~~ 같은 느낌이라 보면 될 듯          result = new ArrayList<ContactData>();          if (cursor.moveToFirst()) {              do {                  //...Set datas to ContactData object              } while (cursor.moveToNext());          }      } catch (Exception e) {          e.printStackTrace();          result = null;      }      return result;  }
    ```
    
- 내부 연락처에서 정보를 가져와 Adapter상의 ContactData list에 불러오는 과정은 JVM에 별도로 Thread를 할당한 뒤 결과값을 Handler로 받아오도록 구현했습니다.
    
    ```
    //ContactAdapter.javaHandler hd = new Handler(){            @Override            public void handleMessage(@NonNull Message msg) {                super.handleMessage(msg);                contactDatas = (ArrayList<ContactData>) msg.obj;                notifyDataSetChanged();            }};new Thread(){    @Override    public void run() {        super.run();        contactDatas = getContactData(retrieve);        Message msg = hd.obtainMessage(1, contactDatas);        hd.sendMessage(msg);    }}.start();
    ```
    
- Contact Fragment — Listener
    1. 상단의 검색창 텍스트 변경 시 retrieve함수를 호출하여 실시간으로 검색 및 출력합니다.
        
        ```
        //Contact.javapublic void afterTextChanged(Editable editable) {    recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contacts);    ContactAdapter contactAdapter = (ContactAdapter) recyclerView.getAdapter();    assert contactAdapter != null;    contactAdapter.retrieveContact(editable.toString());}
        ```
        
    2. 하단의 추가 버튼 클릭 시 연락처 추가 Activity로 이동합니다.
        
        ```
        //Contact.javapublic void onClick(View view) {    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);    requireContext().startActivity(intent);}
        ```
        
- Recyclerview — Listener
    1. ItemView를 길게 클릭 시 연락처 수정 및 삭제에 대한 Dialog 호출합니다. 선택에 따라 해당하는 동작 수행합니다.
        
        ```
        //ContactAdapter.java@Overridepublic boolean onLongClick(View view) {    AlertDialog.Builder builder = new AlertDialog.Builder(context);    builder.setItems(putOrDeleteMenu, new DialogInterface.OnClickListener() {        @Override        public void onClick(DialogInterface dialogInterface, int menuPosition) {            if (menuPosition == 0) {                context.startActivity(new Intent(Intent.ACTION_EDIT, Uri.parse(ContactsContract.Contacts.CONTENT_URI + "/" + Long.toString(indivContact.getContact_id()))));                notifyItemChanged(position);            }            else if (menuPosition == 1) {                contactDatas.remove(position); //db뿐만 아니라 recyclerview 내의 데이터도 삭제해줘야 함                notifyItemRemoved(position);            }        }    });    builder.setNegativeButton("취소", null);    builder.show();    return true;}
        ```
        
    2. Swipe event의 경우 SwipeController 클래스를 추가적으로 구현한 뒤 Recyclerview에 붙여서 처리했습니다. SwipeController 는 ItemtouchHelper.Callback 클래스를 상속한 클래스이며 swipe관련 method를 오버라이딩하여 사용하는 Class입니다.
        
        ```
        //SwipeController.java@Override    public void onChildDraw(Canvas c,                            RecyclerView recyclerView,                            RecyclerView.ViewHolder viewHolder,                            float dX, float dY,                            int actionState, boolean isCurrentlyActive) {        if (actionState == ACTION_STATE_SWIPE) {            drawButtons(c, viewHolder, (int)dX);            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive); //여기조건걸            if (dX >= buttonWidth|| dX <= -buttonWidth){                return;            }        }        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);    }
        ```
        
        Swipe event를 통한 변위에 따라 메시지, 통화에 대한 안내문이 출력됩니다. 손가락을 뗐을 때에는 진동과 함께 통화 혹은 메시지에 대한 activity를 호출합니다.
        
        ```
        //SwipeController.java@Overridepublic boolean onTouch(View v, MotionEvent event) {    swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;    if (swipeBack && !currentTaskon) {        currentTaskon = true;        if (buttonWidth < dX){            call(viewHolder);            activateVibrator();            recyclerView.getChildAt(viewHolder.getAdapterPosition()).setX(buttonWidth);        }        else if (-buttonWidth > dX){            message(viewHolder);            activateVibrator();            recyclerView.getChildAt(viewHolder.getAdapterPosition()).setX(-buttonWidth);        }        currentTaskon = false;    }    return false;}
        ```
        

---

---

### Gallery

---

Gallery의 경우 Fragment - RecyclerView - Item(Fragment - RecuclerView - Item)의 이중 RecyclerView로 구성하였다. 

![그림1.png](Readme%20f32e0e70f4f84883a18fbcdf4bef5aaf/%E1%84%80%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B71.png)

         

RecyclerView의 Item으로 Fragment를 바인딩 한 후 다시 내부에 RecyclerView를 넣은 이유는, LiveData를 사용함에 있어 앨범 별로 이미지를 따로 다루기 위해 앨범마다 ImageViewModel을 할당해야 했기 때문이다.

DB를 사용한 이유는 어플리케이션을 실행할 때 마다 매번 MediaStore에서 Load하게 되면 사용자 경험에 좋지 않은 영향을 끼칠 것이라 판단하였기 때문이다. 어플이 처음 실행될 때, 그리고 Refresh버튼을 클릭할 때 MediaStore를 이용해 Storage와 DB를 연동하고, Image의 Uri는 LiveData를 이용해 DB에서 쿼리해 오도록 설계하였다. 이미지 로드시 뷰를 실시간으로 업데이트 하여, 사용자로 하여금 로딩이 크게 체감되지 않도록 하였다.

![DataFlow.png](Readme%20f32e0e70f4f84883a18fbcdf4bef5aaf/DataFlow.png)

다만 Item에 Fragment를 바인딩 할 때, 화면에 “Draw”되지 않은 View에 Fragment add/replace가 되지 않는 현상이 있어 onDrawListener를 활용하여 ViewHolder가 화면에 Draw되는 시점에 Fragment를 바인딩한다. 아래는 해당 코드부인 AlbumAdapter의 onBindViewHolder 함수이다.

```jsx
@Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {

        final AlbumUri albumUri = albumUris.get(position);

        //고유 id를 발급받아 뷰에 적용시켜야 서로 다른 fragment를 붙일 수 있음.
        holder.itemView.setId(View.generateViewId());

        int containerId = holder.itemView.getId();
        Fragment oldFragment = fm.findFragmentById(containerId);
        if (oldFragment != null) {
            fm.beginTransaction().remove(oldFragment).commit();
        }

        holder.drawed = 0;
        //Fragment는 draw 상태인 View에만 붙일 수 있다. 
				//따라서 DrawListener에서 바인딩을 해주면 된다.
        //그냥 하면 뷰 없다고 에러 발생
        AlbumFragment albumFragment 
								= AlbumFragment.newInstance(albumUri.getAlbumPath(),
																						albumUri.getAlbumName(), 
																						context);
        holder.itemView.getViewTreeObserver()
											 .addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                if(holder.drawed == 0) {
                    fm.beginTransaction()
																		.replace(containerId, albumFragment)
																		.commit();
                    holder.drawed = 1;
                }
            }
        });
    }
```

---

---

### Map

---

![Map.drawio.png](Project1_Readme%206d00ec2adbb14a529616aa2db3d7f817/Map.drawio.png)

- MapFragment는 OnMapReadyCallback인터페이스를 구현하는 Fragment입니다. 네이버 지도 안드로이드 SDK로부터 지도를 불러오는 작업이 완료되면 onMapReady함수에서 FusedLocationSource와 LocationTrackingMode등 지도에 필요한 설정을 마친 후 지도를 표시합니다.
    
    ```java
    //MapFragment.java
    
    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(fusedLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        });
        currentNaverMap = naverMap;
        mapSearchAdapter.setCurrentMap(currentNaverMap);
    }
    ```
    
- 지도를 포함한 Map Fragment를 기반으로 세 번째 탭의 View를 구성하였습니다. EditText를 포함한 UI에서 검색을 실행할 시 기본 검색/근처 검색 설정 여부에 따라 네이버 지역 검색 API에 각각 다른 Query Parameter로 요청을 보냈습니다.
    
    ```java
    //MapFragment.java
    
    String query="";
    Address address = list.get(0);
    if (isCurrentLocationMode > 0){
        query = address.getAddressLine(0) + " " + additionalQuery;
    } else{
        query = additionalQuery;
    }
    String[] params = {query};
    new SearchTask().execute(params);
    ```
    
- 검색 버튼이 입력을 받았을 경우 HTTP Connection에 대한 작업을 수행하는 것이기 때문에 Fragment에서 SearchTask를 AsyncTask로 실행합니다. HttpConnection에 대한 헤더 설정 및  QueryString을 추가해주는 과정을 거치게 됩니다.
    
    ```java
    //MapFragment.java
    private class SearchTask extends AsyncTask<String, Void, ArrayList<SearchResult>> {
        String baseURL = "https://openapi.naver.com/v1/search/local.json";
        URL searchURL;
        String query;
        HttpURLConnection connection;
    
        @Override
        protected ArrayList<SearchResult> doInBackground(String... params) {
            query = params[0];
            ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
            try {
    
                String utf8query = URLEncoder.encode(query, "utf-8");
                String requestQuery = addQueryString(baseURL, utf8query, "10", "1", "random");
                searchURL = new URL(requestQuery);
                connection = (HttpURLConnection) searchURL.openConnection();
                if (connection != null){
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true); //no doOutPut
                    connection.setRequestProperty("HOST", "openapi.naver.com");
                    connection.setRequestProperty("Content-Type", "plain/text");
    								//...
    								//Request Headers
                    int code = connection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null){
                            stringBuilder.append(line+"\n");
                        }
                        bufferedReader.close();
                        String result = stringBuilder.toString();
                        JSONObject responseObject = new JSONObject(result);
                        JSONArray jsonArray = (JSONArray) responseObject.get("items");
                        for (int i = 0; i < jsonArray.length(); i++){
                            //..Add Json Results to JsonObject
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return searchResults;
        }
    ```
    
- 검색에 대한 결과는 RecyclerView로 제공됩니다. 이 때 해당 결과 뷰를 길게 터치하거나 검색 결과를 따라서 맵에 표기된 마커를 터치하면 해당 위치로 줌 인과 함게 이동합니다. 이 때 네이버 지역 검색 API가 제공하는 카텍 좌표계와 moveCamera메서드가 기준으로 삼는 위도, 경도 좌표계가 다르기 때문에 이에 대한 변환 절차가 필요합니다.
    
    ```java
    //MapFragment.java
    
    public LatLng translateCoordinate(int coord_x, int coord_y){
        GeoTransPoint oKA = new GeoTransPoint(coord_x, coord_y);
        GeoTransPoint oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, oKA);
        double lat = oGeo.getY();
        double lng = oGeo.getX();
    
        return new LatLng(lat, lng);
    }
    ```
    
    해당 메서드에 이용된 GeoTransPoint와 GeoTrans클래스는 오픈소스를 참고하였습니다.
    
- Recyclerview상에 표기된 버튼을 터치 시 지역 검색 API를 이용했을 때와 같은 방식으로 Fragment내부에 다시 Fragment View 및 Recyclerview가 표기됩니다. 해당 view는 다른 영역을 터치 시 사라집니다.
    
    ```java
    //MapSearchAdapter.java
    
    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
        myImage.setColorFilter(Color.parseColor("#00ABCAB2"), PorterDuff.Mode.SRC_OVER);
        ReviewDisplayFragment e = new ReviewDisplayFragment(individSearchResult.getTitle());
        e.show(((FragmentActivity)currentContext).getSupportFragmentManager(), "event");
    }
    ```
    
- 생성된 Fragment내에는 블로그 리뷰들에 대한 미리보기들이 view로 나열되어 있으며, 이를 터치 시 해당 블로그 링크에 대한 인터넷 브라우저 접속으로 이어집니다.
    
    ```java
    //ReviewAdapter.java
    
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(individReviewData.getLink()));
        currentContext.startActivity(intent);
    }
    ```