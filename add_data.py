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
    core.add_entry_to_data(data, first_player, second_player, games, wins)
    core.store_data(name, started, data)
