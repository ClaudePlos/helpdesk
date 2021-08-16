Stream:
List<Object> listAfterFilter = listDocKpKw.stream().filter( document -> document.getDocId().equals(docId)).collect(Collectors.toList()) // to list listDocKpKw to jset List<Document>
listDocKpKw.stream().filter( document -> document.getDocId().equals(docId)).collect(Collectors.toList()).get(0)  //get single result	
