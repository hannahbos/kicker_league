import h5py_wrapper.wrapper as h5w
import time

LEAGUE = 'season15'

try:
    # load old data
    data = h5w.load_h5('./%s.h5'%(LEAGUE))
except IOError:
    # create new dataset if not existing
    print 'No dataset %s found. Creating new.'%(LEAGUE)
    data = {}
    data['start_date'] = time.time()
    h5w.add_to_h5('./%s.h5'%(LEAGUE), data, overwrite_dataset=True)

print 'Enter results (0 for exit).'
while True:
    data = h5w.load_h5('./%s.h5'%(LEAGUE))
    team_games_wins = raw_input('Enter team (p1/p2-games-wins): ')
    if team_games_wins in ['', '0']:
        break
    team, games, wins = team_games_wins.split('-')
    games = int(games)
    wins = int(wins)
    assert(wins <= games), 'Can not have more wins than games.'
    team_srt = ''.join(sorted(team.upper().split('/')))
    if team_srt in data.keys():
        data[team_srt]['games'] += games
        data[team_srt]['wins'] += wins
    else:
        data[team_srt] = {}
        data[team_srt]['games'] = games
        data[team_srt]['wins'] = wins
    h5w.add_to_h5('./%s.h5'%(LEAGUE), data, overwrite_dataset=True)
    print 'Updated data.'
