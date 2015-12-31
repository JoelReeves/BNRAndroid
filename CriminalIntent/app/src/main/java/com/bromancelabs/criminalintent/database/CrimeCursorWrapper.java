package com.bromancelabs.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bromancelabs.criminalintent.database.CrimeDbSchema.CrimeTable;
import com.bromancelabs.criminalintent.models.Crime;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        long contactId = getLong(getColumnIndex(CrimeTable.Cols.CONTACT_ID));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        crime.setContactId(contactId);

        return crime;
    }
}
