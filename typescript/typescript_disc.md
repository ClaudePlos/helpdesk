## Query to JSON
let users;</br>
users = await conn.execute(
        'select id_operator id, kod username, haslo password from operatorzy', []</br>, { outFormat: oracledb.OUT_FORMAT_OBJECT }
      );
