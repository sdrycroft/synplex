# Synplex

Synplex, a portmanteau of Synchronise and Plex is intended to make the process
of syncing the view status of a managed user with an admin user much simpler.

## Usage

Note, the example below were run on a Linux system which has the database file
located at the expected default location.

### Help

```bash
$ java -jar ~/bin/synplex-1.0.0.jar -h
usage: Synplex [-h] [-d PATH] [-l] [-a ID [ID ...]] [-s] [-w SECONDS]

Synchronise Plex viewing.

Simple application that will synchronise the viewing stats between a list of
accounts and the main admin account. Originally created as a way of keeping the
admin account (mum and dad) and a single managed account (girl and boy) in sync.


named arguments:
  -h, --help             show this help message and exit
  -d PATH, --database-file PATH
                         Path  to  the  'com.plexapp.plugins.library.db'   database   file.   Defaults   to   '/var/lib/plexmediaserver/Library/Application  Support/Plex  Media  Server/Plug-in
                         Support/Databases/com.plexapp.plugins.library.db'

List accounts:
  -l, --list-accounts    List the accounts that can be copied. Used for getting a list of IDs for the main process.

Synchronise accounts:
  -a ID [ID ...], --accounts ID [ID ...]
                         IDs of the accounts to sync.
  -s, --single-run       Whether the application should exit after the first run, or run continuously.
  -w SECONDS, --wait SECONDS
                         Time to wait in seconds between each synchronisation. Defaults to 300 seconds.
```

### List the accounts

Useful for finding the IDs for the main sync process.

```bash
$ java -jar ~/bin/synplex-1.0.0.jar -l
Accounts
           sdrycroft :            1
            monkey99 :     12345678
           monkey100 :     12345679
```

### Synchronise the accounts

The following will synchronise the views between the admin account (1) and the
two listed accounts. Effectively all three accounts will have exactly the same
stats.

```bash
$ java -jar ~/bin/synplex-1.0.0.jar -a 12345678 12345679 -w 900
```