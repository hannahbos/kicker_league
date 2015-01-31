import numpy as np

import core

LEAGUE = 'test'

name, started, data = core.load_data(LEAGUE)

print 'Enter results (Return or 0 for exit).'
while True:
    team_games_wins = raw_input('Enter team (p1/p2-games-wins): ')
    if team_games_wins in ['', '0']:
        break
    # quite painful way to seperate user input to relevant fields
    # maybe there is a better way
    team, games, wins = team_games_wins.split('-')
    games = int(games)
    wins = int(wins)
    assert(wins <= games), 'Can not have more wins than games.'
    first_player, second_player = sorted(team.upper().split('/'))
    # check whether this combination exists
    if first_player in data['1st'] and second_player in data['2nd']:
        # update if existing
        idx = np.where(np.logical_and(first_player == data['1st'],
                                      second_player == data['2nd']))[0]
        data['games'][idx] += games
        data['wins'][idx] += wins
    else:
        # create new entry if not existing
        data = np.append(data, np.array([(first_player, second_player, games, wins)], dtype=core.DTYPE))
    core.store_data(name, started, data)
