## Query to JSON
let users;
users = await conn.execute(
        'select id_operator id, kod username, haslo password from operatorzy', [], { outFormat: oracledb.OUT_FORMAT_OBJECT }
      );
