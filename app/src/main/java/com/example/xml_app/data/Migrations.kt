package com.example.xml_app.data

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            Create table addresses (
                uid INTEGER PRIMARY KEY AUTOINCREMENT not null,
                user_id Integer not null,
                full_name text not null,
                full_address text not null,
                phone_number text not null,
                label text not null,
                is_default_address integer not null,
                is_default_shipping_address integer not null,
                foreign key (user_id) references users(uid) on delete cascade
            )
        """.trimIndent()
        )

        connection.execSQL(
            """
                create index index_adresses_user_id on addresses(user_id)
            """.trimIndent()
        )

        connection.execSQL(
            """
                create table users_new(
                    uid integer not null,
                    firebase_uid text not null,
                    full_name text not null,
                    username text not null,
                    primary key(uid)
                )
            """.trimIndent()
        )

        connection.execSQL(
            """
                insert into users_new(
                uid, firebase_uid, full_name, username
                )
                select uid, firebase_uid, full_name, username from users
            """.trimIndent()
        )

        connection.execSQL(
            "drop table users"
        )

        connection.execSQL(
            "alter table users_new rename to users"
        )

        connection.execSQL(
            """
                create unique index index_users_firebase_uid on users(firebase_uid)
            """.trimIndent()
        )
    }
}

val MIGRATION2_3 = object : Migration(2, 3) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
               alter table users add column email text not null default ''
           """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
                create table search_history(
                    uid Integer Primary Key Autoincrement not null,
                    user_id integer not null,
                    query text not null,
                    searched_at integer not null,
                    foreign key (user_id) references users(uid) on delete cascade
                )
            """.trimIndent()
        )

        connection.execSQL(
            """
                create index index_search_history_id on search_history(user_id)
            """.trimIndent()
        )

        connection.execSQL(
            """
                create unique index index_search_history_user_id_query on search_history(user_id, query)
            """.trimIndent()
        )
    }
}