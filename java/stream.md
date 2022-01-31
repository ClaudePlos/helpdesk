## Stream:</br></br>
<pre>
List<Object> listAfterFilter = listDocKpKw.stream().filter( document -> document.getDocId().equals(docId)).collect(Collectors.toList()) </br>// to list listDocKpKw to jset List<Document> </br></br>
listDocKpKw.stream().filter( document -> document.getDocId().equals(docId)).collect(Collectors.toList()).get(0)  </br>//get single result	</br></br>
boolean exists = names.stream().anyMatch(x -> Objects.equals(x, n)); </br> // contain

sort by:
import java.util.stream.Collectors;
List<User> sortedList = users.stream()
		.sorted(Comparator.comparing(User::getName))
		.collect(Collectors.toList());


List<Object> listAfterFilter = listDocKpKw.stream().filter( document -> document.getDocId().equals(docId)).collect(Collectors.toList()) // to list listDocKpKw to jset List<Document>
listDocKpKw.stream().filter( document -> document.getDocId().equals(docId)).collect(Collectors.toList()).get(0)  //get single result

cateringCompanies = companies.stream()
                .filter( c -> c.getFrmDesc() != null)
                .filter(c -> c.getFrmDesc().equals("C"))
                .collect(Collectors.toList());
				

List<EkSkladnikDTO> listSkladniku = skladnikService.getList(); 				
// lacze dwa skladniki 5 i 421 tak by scalić to tylko do dsk: 5
listSkladniku.stream().forEach( skl -> {
	if (skl.getDskNazwa().equals("Wyrównanie do minimalnej")) {
		listSkladniku.stream()
				.filter( s -> s.getDskNazwa().equals("Płaca zasad. z urlopami pomn. o chorob."))
				.forEach( s -> s.setWartosc( s.getWartosc() + skl.getWartosc()));
	}
});
listSkladniku.removeIf( s -> s.getDskNazwa().equals("Wyrównanie do minimalnej") ); // usuniecie skladnika min.wyn	


</pre>
