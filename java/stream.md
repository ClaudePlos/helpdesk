## Stream:</br></br>
List<Object> listAfterFilter = listDocKpKw.stream().filter( document -> document.getDocId().equals(docId)).collect(Collectors.toList()) </br>// to list listDocKpKw to jset List<Document> </br></br>
listDocKpKw.stream().filter( document -> document.getDocId().equals(docId)).collect(Collectors.toList()).get(0)  </br>//get single result	
