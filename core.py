import numpy as np
import hashlib as hsh
import time

# custom dtype for array holding the data
# two fields for player entries with 6 (possible) characters
# two fields for games and wins with 6 (possible) digits
DTYPE = [('1st', 'S6'), ('2nd', 'S6'),
         ('games', 'i6'), ('wins', 'i6')]

# template for header that is written to each data file
# contains name of league, starting date
# and hashsum to guarantee integrity of data
HEADER_TEMPLATE = 'name %s\nstarted %f\nhashsum %s\n1st player, 2nd player, games, wins'


# calculates an md5 hashsum from entries in table
def get_hash(data):
    assert(data.dtype == DTYPE), '[critical] Wrong dtype.'
    raw = ''.join(data['1st'])
    raw += ''.join(data['2nd'])
    raw += ''.join([str(x) for x in data['games']])
    raw += ''.join([str(x) for x in data['wins']])
    return hsh.md5(raw).hexdigest()

# returns stored data or empty data of file is not existing
# perform integrity check of data
def load_data(league, create_new=True):
    try:
        f = open('%s.csv'%league, 'r')
        name = f.readline()[:-1].split(' ')[-1]
        started = float(f.readline()[:-1].split(' ')[-1])
        hashsum = f.readline()[:-1].split(' ')[-1]
        print '[info] Loaded data from %s, started at %s.'\
            %(name, time.ctime(started))
        data = np.loadtxt('%s.csv'%league, dtype=DTYPE, delimiter=',')
        # this check keeps loading from failing if only one entry is present
        if np.shape(data) == ():
            data = np.reshape(data, (1,))
        assert(league == name), \
            '[critical] Data integrity check failed. Corrupt name.'
        assert(started < time.time()), \
            '[critical] Data integrity check failed. Corrupt date.'
        assert(hashsum == get_hash(data)), \
            '[critical] Data integrity check failed. Corrupt data.'
        print '[info] Data integrity check succeeded.'
        return name, started, data
    except IOError, e:
        if create_new:
            print '[info] No data for %s found. Creating new file.'%(league)
            return league, time.time(), np.empty(0, DTYPE)
        else:
            raise e

# writes data to csv file with appropriate header
def store_data(name, started, data):
    header = HEADER_TEMPLATE%(name, started, get_hash(data))
    np.savetxt('%s.csv'%name, data, fmt='%s,%s,%d,%d',
               comments='# ', header=header)
    print '[info] Data for %s stored.'%(name)

# add result for first player, second player with games and wins to dataset
def add_entry_to_data(data, first_player, second_player, games, wins):
    if first_player in data['1st'] and second_player in data['2nd']:
        # update if existing
        idx = np.where(np.logical_and(first_player == data['1st'],
                                      second_player == data['2nd']))[0]
        data['games'][idx] += games
        data['wins'][idx] += wins
    else:
        # create new entry if not existing
        data = np.append(data, np.array([(first_player, second_player, games, wins)], dtype=DTYPE))
